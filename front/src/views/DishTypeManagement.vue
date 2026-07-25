<template>
  <div class="dish-type-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <div class="card-header-text">
            <span class="card-title">菜品种类管理</span>
            <span class="card-subtitle">维护菜品分类与分类图标</span>
          </div>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增种类
          </el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="种类名称">
            <el-input
              v-model="searchForm.name"
              placeholder="请输入种类名称"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格 -->
      <SkeletonScreen v-if="loading" variant="table" :count="5" />
      <el-table v-else :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column label="图标" width="100" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.iconUrl"
              v-lazy
              lazy
              :src="row.iconUrl"
              :preview-src-list="[row.iconUrl]"
              fit="cover"
              class="type-icon"
            />
            <span v-else>暂无图标</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="种类名称" min-width="150" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
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
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增菜品种类' : '编辑菜品种类'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="种类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入种类名称" />
        </el-form-item>
        <el-form-item label="图标" prop="iconUrl">
          <div class="icon-upload">
            <el-image
              v-if="form.iconUrl"
              :src="form.iconUrl"
              fit="cover"
              class="preview-icon"
            />
            <el-upload
              :show-file-list="false"
              :before-upload="beforeIconUpload"
              :http-request="handleIconUpload"
              accept="image/*"
            >
              <el-button type="primary">
                <el-icon><Upload /></el-icon>
                {{ form.iconUrl ? '更换图标' : '上传图标' }}
              </el-button>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules, UploadRequestOptions } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, Upload } from '@element-plus/icons-vue'
import { getDishTypeList, saveDishType, updateDishType, deleteDishType } from '@/api/dish'
import { uploadFile } from '@/api/user'
import SkeletonScreen from '@/components/SkeletonScreen.vue'
import type { DishType } from '@/types/dish'

// 搜索表单
const searchForm = reactive({
  name: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref<DishType[]>([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

// 表单数据
const form = reactive({
  id: 0,
  name: '',
  iconUrl: ''
})

// 表单验证规则
const rules: FormRules = {
  name: [
    { required: true, message: '请输入种类名称', trigger: 'blur' },
    { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
  ],
  iconUrl: [
    { required: true, message: '请上传图标', trigger: 'change' }
  ]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getDishTypeList({
      current: pagination.current - 1,
      size: pagination.size,
      name: searchForm.name || undefined
    })
    if (res.code === 200) {
      tableData.value = res.data || []
      pagination.total = res.count || 0
    } else {
      ElMessage.error(res.message || '查询失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '网络错误')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogType.value = 'add'
  form.id = 0
  form.name = ''
  form.iconUrl = ''
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: DishType) => {
  dialogType.value = 'edit'
  form.id = row.id
  form.name = row.name
  form.iconUrl = row.iconUrl
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: DishType) => {
  ElMessageBox.confirm(
    `确定要删除菜品种类"${row.name}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteDishType(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error: any) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {
    // 取消操作
  })
}

// 图标上传前验证
const beforeIconUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 图标上传
const handleIconUpload = async (options: UploadRequestOptions) => {
  try {
    const res = await uploadFile(options.file)
    if (res.code === 200) {
      form.iconUrl = res.data
      ElMessage.success('图标上传成功')
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '上传失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const api = dialogType.value === 'add' ? saveDishType : updateDishType
        const res = await api({
          id: dialogType.value === 'edit' ? form.id : undefined,
          name: form.name,
          iconUrl: form.iconUrl
        })
        if (res.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '新增成功' : '修改成功')
          dialogVisible.value = false
          loadData()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error: any) {
        ElMessage.error(error.message || '操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 分页大小变化
const handleSizeChange = (val: number) => {
  pagination.size = val
  pagination.current = 1
  loadData()
}

// 页码变化
const handleCurrentChange = (val: number) => {
  pagination.current = val
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.dish-type-management {
  padding: 28px 0 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  color: var(--ink-900);
  font-size: 20px;
  font-weight: 750;
  letter-spacing: -0.02em;
}

.box-card :deep(.el-card__header) {
  padding: 18px 24px;
}

.box-card :deep(.el-card__body) {
  padding: 22px 24px 24px;
}

.search-area {
  margin-bottom: 20px;
  padding: 16px;
  border: 1px solid #f0e6dd;
  border-radius: 12px;
  background: #fffaf6;
}

.search-area :deep(.el-form) {
  display: flex;
  gap: 0 4px;
  align-items: flex-end;
}

.search-area :deep(.el-input) {
  width: min(260px, 56vw);
}

.type-icon {
  width: 52px;
  height: 52px;
  overflow: hidden;
  border: 1px solid #f0e4da;
  border-radius: 10px;
  box-shadow: 0 3px 10px rgba(65, 42, 28, 0.1);
}

.icon-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

.preview-icon {
  width: 80px;
  height: 80px;
  border: 1px solid #eadbd0;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(65, 42, 28, 0.08);
}

.pagination {
  margin-top: 22px;
  padding-top: 18px;
  border-top: 1px solid var(--line);
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 640px) {
  .dish-type-management {
    padding-top: 18px;
  }

  .box-card :deep(.el-card__header),
  .box-card :deep(.el-card__body) {
    padding-right: 16px;
    padding-left: 16px;
  }

  .card-header {
    font-size: 18px;
  }

  .search-area {
    padding: 12px;
  }

  .search-area :deep(.el-form) {
    display: block;
  }

  .search-area :deep(.el-form-item) {
    display: flex;
    margin: 0 0 12px;
  }

  .search-area :deep(.el-form-item__content) {
    flex: 1;
  }

  .search-area :deep(.el-input) {
    width: 100%;
  }

  .pagination {
    justify-content: center;
  }
}
</style>
