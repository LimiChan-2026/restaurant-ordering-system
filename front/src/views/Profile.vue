<template>
  <div class="profile-container">
    <div class="profile-card">
      <!-- 页面标题 -->
      <div class="page-header">
        <el-button :icon="ArrowLeft" @click="router.back()">返回</el-button>
        <h2>个人信息</h2>
        <div></div>
      </div>

      <!-- 头像区域 -->
      <div class="avatar-section">
        <div class="avatar-ring">
          <el-avatar v-lazy :size="100" :src="form.avatar" class="avatar">
            <span class="avatar-text">{{ form.username?.charAt(0) || 'U' }}</span>
          </el-avatar>
        </div>
        <el-upload
          class="avatar-upload"
          :show-file-list="false"
          :before-upload="beforeAvatarUpload"
          :http-request="handleAvatarUpload"
          accept="image/*"
        >
          <el-button size="small" type="primary" :loading="avatarLoading">
            <el-icon><Upload /></el-icon>
            更换头像
          </el-button>
        </el-upload>
      </div>

      <!-- 个人信息表单 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="profile-form"
      >
        <el-form-item label="账号">
          <el-input :model-value="authStore.userInfo?.account" disabled />
        </el-form-item>

        <el-form-item label="昵称" prop="username">
          <el-input v-model="form.username" placeholder="请输入昵称" />
        </el-form-item>

        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :value="0">未知</el-radio>
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="生日">
          <el-date-picker
            v-model="form.birthday"
            type="date"
            placeholder="请选择生日"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSave">
            保存修改
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { ArrowLeft, Upload } from '@element-plus/icons-vue'
import { updateUser, uploadFile } from '@/api/user'
import { useAuthStore } from '@/stores/auth'
import type { UpdateUserParams } from '@/types/user'

const router = useRouter()
const authStore = useAuthStore()

// 表单引用
const formRef = ref<FormInstance>()

// 加载状态
const loading = ref(false)
const avatarLoading = ref(false)

// 表单数据
const form = reactive<UpdateUserParams>({
  id: 0,
  username: '',
  avatar: '',
  gender: 0,
  birthday: '',
  phone: '',
  email: ''
})

// 表单验证规则
const rules: FormRules = {
  username: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 初始化表单数据
onMounted(() => {
  if (authStore.userInfo) {
    const { id, username, avatar, gender, birthday, phone, email } = authStore.userInfo
    form.id = id
    form.username = username || ''
    form.avatar = avatar || ''
    form.gender = gender ?? 0
    form.birthday = birthday || ''
    form.phone = phone || ''
    form.email = email || ''
  }
})

// 头像上传前校验
const beforeAvatarUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 处理头像上传
const handleAvatarUpload = async (options: any) => {
  avatarLoading.value = true
  try {
    const res = await uploadFile(options.file)
    const currentUser = authStore.userInfo
    if (!currentUser) return

    const avatar = res.data
    await updateUser({
      id: currentUser.id,
      username: currentUser.username,
      avatar,
      gender: currentUser.gender ?? 0,
      birthday: currentUser.birthday || '',
      phone: currentUser.phone || '',
      email: currentUser.email || ''
    })
    form.avatar = avatar
    authStore.updateUserInfo({ avatar })
    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('头像上传失败:', error)
  } finally {
    avatarLoading.value = false
  }
}

// 保存修改
const handleSave = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await updateUser(form)
      // 更新本地存储的用户信息
      authStore.updateUserInfo({
        username: form.username,
        avatar: form.avatar,
        gender: form.gender,
        birthday: form.birthday,
        phone: form.phone,
        email: form.email
      })
      ElMessage.success('保存成功')
    } catch (error) {
      console.error('保存失败:', error)
    } finally {
      loading.value = false
    }
  })
}

// 重置表单
const handleReset = () => {
  if (authStore.userInfo) {
    const { username, avatar, gender, birthday, phone, email } = authStore.userInfo
    form.username = username || ''
    form.avatar = avatar || ''
    form.gender = gender ?? 0
    form.birthday = birthday || ''
    form.phone = phone || ''
    form.email = email || ''
  }
}
</script>

<style scoped>
.profile-container {
  min-height: calc(100vh - 68px);
  padding: 34px 20px 44px;
}

.profile-card {
  position: relative;
  overflow: hidden;
  max-width: 650px;
  margin: 0 auto;
  background: #fff;
  border: 1px solid #eee3da;
  border-radius: 18px;
  padding: 34px;
  box-shadow: 0 10px 28px rgba(65, 42, 28, 0.08);
}

.profile-card::before {
  position: absolute;
  top: 0;
  right: 0;
  left: 0;
  height: 5px;
  background: var(--gradient-brand, linear-gradient(135deg, #c9502d, #e77b4e));
  content: '';
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
}

.page-header h2 {
  margin: 0;
  color: #30241f;
  font-size: 24px;
  font-weight: 750;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
  padding-bottom: 32px;
  border-bottom: 1px solid #f0e6df;
}

.avatar-ring {
  padding: 4px;
  border-radius: 50%;
  background: var(--gradient-brand, linear-gradient(135deg, #c9502d, #e77b4e));
  box-shadow: 0 10px 22px rgba(201, 80, 45, 0.22);
}

.avatar {
  display: block;
  border: 3px solid #fff;
  background: linear-gradient(135deg, #c9502d 0%, #e77b4e 100%);
}

.avatar-text {
  font-size: 36px;
  color: #fff;
}

.profile-form {
  max-width: 400px;
  margin: 0 auto;
}

.profile-form :deep(.el-form-item__label) {
  font-weight: 500;
}

.profile-form :deep(.el-input.is-disabled .el-input__wrapper) {
  background-color: #fff6f0;
}
@media (max-width: 700px) {.profile-container{min-height:calc(100vh - 60px);padding:22px 14px 34px}.profile-card{padding:24px 18px;border-radius:16px}.page-header{margin-bottom:24px}.page-header h2{font-size:22px}}
</style>
