<template>
  <div class="register-container">
    <div class="auth-orb auth-orb--1 float-orb"></div>
    <div class="auth-orb auth-orb--2 float-orb"></div>
    <div class="auth-orb auth-orb--3"></div>
    <div class="register-card">
      <!-- Logo -->
      <div class="logo-section">
        <div class="logo-icon">🍽️</div>
        <h1 class="logo-title">餐厅订餐系统</h1>
        <p class="logo-subtitle">创建新账号</p>
      </div>

      <!-- 注册表单 -->
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        @keyup.enter="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            size="large"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-btn"
            :loading="loading"
            @click="handleRegister"
          >
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 登录链接 -->
      <div class="login-link">
        已有账号？
        <router-link to="/login" class="link">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { register } from '@/api/user'

const router = useRouter()

// 表单引用
const registerFormRef = ref<FormInstance>()

// 加载状态
const loading = ref(false)

// 注册表单数据（用户名同时作为登录账号）
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

// 确认密码验证器
const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await register({
        username: registerForm.username,
        password: registerForm.password,
        confirmPassword: registerForm.confirmPassword
      })

      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } catch (error) {
      // 错误已在拦截器中处理
      console.error('注册失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.register-container {
  position: relative;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px;
  overflow: hidden;
  background:
    radial-gradient(circle at 15% 20%, rgba(255, 210, 179, 0.36), transparent 28rem),
    radial-gradient(circle at 82% 85%, rgba(232, 112, 64, 0.28), transparent 26rem),
    linear-gradient(135deg, #2f1d19 0%, #6b3024 52%, #b54b2e 100%);
}

/* 漂浮装饰光斑 */
.auth-orb {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.auth-orb--1 {
  width: 340px;
  height: 340px;
  top: -120px;
  left: -80px;
  border: 1px solid rgba(255, 226, 205, 0.22);
  box-shadow:
    0 0 0 38px rgba(255, 226, 205, 0.05),
    0 0 0 84px rgba(255, 226, 205, 0.03);
}

.auth-orb--2 {
  width: 240px;
  height: 240px;
  right: -70px;
  bottom: 10%;
  background: radial-gradient(circle, rgba(255, 196, 158, 0.3), transparent 70%);
}

.auth-orb--3 {
  width: 130px;
  height: 130px;
  left: 11%;
  bottom: -34px;
  background: radial-gradient(circle, rgba(255, 236, 220, 0.2), transparent 70%);
}

@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(26px) scale(0.985);
  }

  to {
    opacity: 1;
    transform: none;
  }
}

.register-card {
  position: relative;
  width: 400px;
  padding: 44px 42px 40px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.62);
  border-radius: 24px;
  background: rgba(255, 253, 251, 0.92);
  backdrop-filter: blur(20px);
  box-shadow: 0 30px 80px rgba(30, 12, 7, 0.34);
  animation: card-in 0.55s var(--ease-out, ease) both;

  &::before {
    position: absolute;
    width: 170px;
    height: 170px;
    top: -92px;
    right: -62px;
    border-radius: 50%;
    background: radial-gradient(circle, #ffe9db 0%, #fff0e8 60%, transparent 100%);
    content: '';
  }
}

.logo-section {
  position: relative;
  text-align: center;
  margin-bottom: 34px;
}

.logo-icon {
  display: grid;
  width: 64px;
  height: 64px;
  margin: 0 auto 14px;
  border-radius: 20px;
  background: linear-gradient(135deg, #c9502d, #e9794e);
  box-shadow: 0 10px 21px rgba(201, 80, 45, 0.25);
  font-size: 31px;
  place-items: center;
}

.logo-title {
  color: #33241f;
  font-size: 25px;
  font-weight: 750;
  margin: 0 0 8px 0;
}

.logo-subtitle {
  font-size: 14px;
  color: #8b7b72;
  margin: 0;
}

.register-form {
  margin-bottom: 16px;
}

.register-form :deep(.el-input__wrapper) {
  min-height: 46px;
  border-radius: 12px;
  transition: box-shadow 0.2s ease;
}

.register-form :deep(.el-input__wrapper.is-focus) {
  box-shadow:
    0 0 0 1px #df6237 inset,
    0 0 0 4px rgba(223, 98, 55, 0.14) !important;
}

.register-btn {
  width: 100%;
  min-height: 46px;
  border-radius: 12px;
  font-size: 16px;
  letter-spacing: 0.35em;
  text-indent: 0.35em;
}

.login-link {
  text-align: center;
  color: #7a6c64;
  font-size: 14px;
}

.login-link .link {
  color: #bf482a;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.2s ease;
}

.login-link .link:hover {
  color: #df6237;
  text-decoration: underline;
}

@media (max-width: 480px) {
  .register-container {
    padding: 16px;
  }

  .register-card {
    width: 100%;
    padding: 34px 24px;
    border-radius: 18px;
  }
}
</style>
