<template>
  <div class="user-management-container">
    <div class="page-card">
      <!-- 页面标题 -->
      <div class="page-header">
        <div class="card-header-text">
          <h2>用户管理</h2>
          <span class="card-subtitle">管理平台注册用户、角色与账号状态</span>
        </div>
      </div>

      <!-- 搜索区域 -->
      <div class="search-section">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="用户名">
            <el-input
              v-model="searchForm.username"
              placeholder="请输入用户名"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="searchForm.role" placeholder="请选择角色" clearable>
              <el-option label="普通用户" :value="1" />
              <el-option label="管理员" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
            <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 用户列表表格 -->
      <SkeletonScreen v-if="loading" variant="table" :count="6" />
      <el-table
        v-else
        :data="userList"
        border
        stripe
        class="user-table"
      >
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column label="头像" width="80" align="center">
          <template #default="{ row }">
            <el-avatar v-lazy :size="40" :src="row.avatar">
              {{ row.username?.charAt(0) || 'U' }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="account" label="账号" min-width="120" />
        <el-table-column label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 2 ? 'danger' : 'success'" size="small">
              {{ row.role === 2 ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status !== false ? 'success' : 'danger'" size="small">
              {{ row.status !== false ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" align="center" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button
              :type="row.status !== false ? 'danger' : 'success'"
              link
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status !== false ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑用户"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="editForm.role" placeholder="请选择角色">
            <el-option label="普通用户" :value="1" />
            <el-option label="管理员" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="handleSaveEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getUserList, adminUpdateUser } from '@/api/user'
import SkeletonScreen from '@/components/SkeletonScreen.vue'
import type { UserInfo } from '@/types/user'

// 加载状态
const loading = ref(false)
const editLoading = ref(false)

// 用户列表
const userList = ref<UserInfo[]>([])

// 搜索表单
const searchForm = reactive({
  username: '',
  role: null as number | null
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 编辑对话框
const editDialogVisible = ref(false)
const editFormRef = ref<FormInstance>()
const editForm = reactive({
  id: 0,
  username: '',
  role: 2
})

// 编辑表单验证规则
const editRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      current: pagination.current - 1, // 接口从0开始
      size: pagination.size,
      username: searchForm.username || undefined,
      role: searchForm.role
    })
    userList.value = res.data || []
    pagination.total = res.count || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchUserList()
}

// 重置搜索
const handleReset = () => {
  searchForm.username = ''
  searchForm.role = null
  pagination.current = 1
  fetchUserList()
}

// 分页大小变化
const handleSizeChange = () => {
  pagination.current = 1
  fetchUserList()
}

// 页码变化
const handleCurrentChange = () => {
  fetchUserList()
}

// 编辑用户
const handleEdit = (row: UserInfo) => {
  editForm.id = row.id
  editForm.username = row.username
  editForm.role = row.role
  editDialogVisible.value = true
}

// 保存编辑
const handleSaveEdit = async () => {
  if (!editFormRef.value) return

  await editFormRef.value.validate(async (valid) => {
    if (!valid) return

    editLoading.value = true
    try {
      await adminUpdateUser({
        id: editForm.id,
        username: editForm.username,
        role: editForm.role
      })
      ElMessage.success('修改成功')
      editDialogVisible.value = false
      fetchUserList()
    } catch (error) {
      console.error('修改失败:', error)
    } finally {
      editLoading.value = false
    }
  })
}

// 切换用户状态（禁用/启用）
const handleToggleStatus = async (row: UserInfo) => {
  const newStatus = row.status === false ? true : false
  const actionText = newStatus ? '启用' : '禁用'

  try {
    await ElMessageBox.confirm(
      `确定要${actionText}用户 "${row.username}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await adminUpdateUser({
      id: row.id,
      status: newStatus
    })
    ElMessage.success(`${actionText}成功`)
    fetchUserList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${actionText}失败:`, error)
    }
  }
}

// 初始化
onMounted(() => {
  fetchUserList()
})
</script>

<style scoped>
.user-management-container {
  padding: 28px 0 8px;
}

.page-card {
  overflow: hidden;
  border: 1px solid rgba(231, 221, 213, 0.94);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: var(--shadow-sm);
}

.page-header {
  margin-bottom: 20px;
  padding: 20px 24px;
  border-bottom: 1px solid var(--line);
  background: linear-gradient(90deg, #fffdfb, #fff8f3);
}

.page-header h2 {
  margin: 0;
  color: var(--ink-900);
  font-size: 20px;
  font-weight: 750;
  letter-spacing: -0.02em;
}

.search-section {
  margin: 0 24px 20px;
  padding: 16px;
  border: 1px solid #f0e6dd;
  border-radius: 12px;
  background: #fffaf6;
}

.search-form {
  display: flex;
  gap: 0 4px;
  align-items: flex-end;
}

.search-form :deep(.el-input) {
  width: min(240px, 52vw);
}

.search-form :deep(.el-select) {
  width: 132px;
}

.user-table {
  width: calc(100% - 48px);
  margin: 0 24px;
}

.pagination-section {
  display: flex;
  justify-content: flex-end;
  margin: 22px 24px 0;
  padding: 18px 0 24px;
  border-top: 1px solid var(--line);
}

@media (max-width: 640px) {
  .user-management-container {
    padding-top: 18px;
  }

  .page-header {
    padding: 18px 16px;
  }

  .page-header h2 {
    font-size: 18px;
  }

  .search-section {
    margin: 0 16px 16px;
    padding: 12px;
  }

  .search-form {
    display: block;
  }

  .search-form :deep(.el-form-item) {
    display: flex;
    margin: 0 0 12px;
  }

  .search-form :deep(.el-form-item__content) {
    flex: 1;
  }

  .search-form :deep(.el-input),
  .search-form :deep(.el-select) {
    width: 100%;
  }

  .user-table {
    width: calc(100% - 32px);
    margin: 0 16px;
  }

  .pagination-section {
    justify-content: center;
    margin: 18px 16px 0;
    padding-bottom: 20px;
  }
}
</style>
