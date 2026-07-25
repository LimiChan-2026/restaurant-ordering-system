<template>
  <div class="dish-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <div class="card-header-text">
            <span class="card-title">菜品管理</span>
            <span class="card-subtitle">维护菜品信息、封面、上下架与套餐</span>
          </div>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增菜品
          </el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="菜品名称">
            <el-input
              v-model="searchForm.name"
              placeholder="请输入菜品名称"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="菜品分类">
            <el-select v-model="searchForm.typeId" placeholder="请选择分类" clearable>
              <el-option
                v-for="item in dishTypeList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="上架" :value="1" />
              <el-option label="下架" :value="0" />
            </el-select>
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
      <SkeletonScreen v-if="loading" variant="table" :count="6" />
      <el-table v-else :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column label="封面" width="80" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.coverUrl"
              v-lazy
              lazy
              :src="row.coverUrl"
              :preview-src-list="[row.coverUrl]"
              fit="cover"
              class="dish-cover"
            />
            <span v-else>暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="菜品名称" min-width="120" />
        <el-table-column prop="typeName" label="分类" width="100" align="center">
          <template #default="{ row }">
            <el-tag>{{ row.typeName || '未分类' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="detail" label="简介" min-width="150" show-overflow-tooltip />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'danger'">
              {{ row.status ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="套餐" width="120" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewPackages(row)">
              查看 ({{ row.dishesPackageList?.length || 0 }})
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center" />
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              :type="row.status ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status ? '下架' : '上架' }}
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

    <!-- 新增/编辑菜品对话框 -->
    <el-dialog
      v-model="dishDialogVisible"
      :title="dishDialogType === 'add' ? '新增菜品' : '编辑菜品'"
      width="700px"
      top="5vh"
    >
      <el-form
        ref="dishFormRef"
        :model="dishForm"
        :rules="dishRules"
        label-width="100px"
      >
        <el-form-item label="菜品名称" prop="name">
          <el-input v-model="dishForm.name" placeholder="请输入菜品名称" />
        </el-form-item>
        <el-form-item label="所属分类" prop="typeId">
          <el-select v-model="dishForm.typeId" placeholder="请选择分类">
            <el-option
              v-for="item in dishTypeList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="封面图" prop="coverUrl">
          <div class="cover-upload">
            <el-image
              v-if="dishForm.coverUrl"
              :src="dishForm.coverUrl"
              fit="cover"
              class="preview-cover"
            />
            <el-upload
              :show-file-list="false"
              :before-upload="beforeCoverUpload"
              :http-request="handleCoverUpload"
              accept="image/*"
            >
              <el-button type="primary">
                <el-icon><Upload /></el-icon>
                {{ dishForm.coverUrl ? '更换封面' : '上传封面' }}
              </el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="菜品介绍" prop="detail">
          <el-input
            v-model="dishForm.detail"
            type="textarea"
            :rows="3"
            placeholder="请输入菜品介绍"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="dishForm.status" active-text="上架" inactive-text="下架" />
        </el-form-item>

        <!-- 套餐列表 -->
        <el-divider content-position="left">套餐信息</el-divider>
        <div class="package-list">
          <div v-for="(pkg, index) in dishForm.dishesPackageList" :key="index" class="package-item">
            <el-row :gutter="10">
              <el-col :span="7">
                <el-form-item
                  :prop="`dishesPackageList.${index}.name`"
                  :rules="[{ required: true, message: '请输入套餐名', trigger: 'blur' }]"
                >
                  <el-input v-model="pkg.name" placeholder="套餐名" />
                </el-form-item>
              </el-col>
              <el-col :span="7">
                <el-form-item
                  :prop="`dishesPackageList.${index}.specs`"
                  :rules="[{ required: true, message: '请输入规格', trigger: 'blur' }]"
                >
                  <el-input v-model="pkg.specs" placeholder="规格" />
                </el-form-item>
              </el-col>
              <el-col :span="7">
                <el-form-item
                  :prop="`dishesPackageList.${index}.price`"
                  :rules="[
                    { required: true, message: '请输入价格', trigger: 'blur' },
                    { type: 'number', min: 0.01, message: '价格必须大于0', trigger: 'blur' }
                  ]"
                >
                  <el-input-number v-model="pkg.price" :min="0.01" :precision="2" :step="1" />
                </el-form-item>
              </el-col>
              <el-col :span="3">
                <el-button type="danger" @click="removePackage(index)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-col>
            </el-row>
          </div>
          <el-button type="primary" @click="addPackage" class="add-package-btn">
            <el-icon><Plus /></el-icon>
            添加套餐
          </el-button>
        </div>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dishDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleDishSubmit" :loading="submitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 套餐管理对话框 -->
    <el-dialog
      v-model="packageDialogVisible"
      title="套餐管理"
      width="800px"
    >
      <div class="package-header">
        <span>菜品：{{ currentDish?.name }}</span>
        <el-button type="primary" size="small" @click="handleAddPackage">
          <el-icon><Plus /></el-icon>
          新增套餐
        </el-button>
      </div>
      <el-table :data="packageList" border stripe v-loading="packageLoading">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="name" label="套餐名" min-width="100" />
        <el-table-column prop="specs" label="规格" min-width="100" />
        <el-table-column prop="price" label="价格" width="100" align="center">
          <template #default="{ row }">
            <span class="price">¥{{ row.price?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEditPackage(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDeletePackage(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 套餐编辑对话框 -->
    <el-dialog
      v-model="packageEditVisible"
      :title="packageEditType === 'add' ? '新增套餐' : '编辑套餐'"
      width="500px"
    >
      <el-form
        ref="packageFormRef"
        :model="packageForm"
        :rules="packageRules"
        label-width="80px"
      >
        <el-form-item label="套餐名" prop="name">
          <el-input v-model="packageForm.name" placeholder="请输入套餐名" />
        </el-form-item>
        <el-form-item label="规格" prop="specs">
          <el-input v-model="packageForm.specs" placeholder="请输入规格" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="packageForm.price" :min="0.01" :precision="2" :step="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="packageEditVisible = false">取消</el-button>
          <el-button type="primary" @click="handlePackageSubmit" :loading="packageSubmitLoading">
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
import {
  getDishList,
  saveDish,
  updateDish,
  deleteDish,
  updateDishStatus,
  getDishTypeList as fetchDishTypeList,
  getDishPackageList,
  saveDishPackage,
  updateDishPackage,
  deleteDishPackage
} from '@/api/dish'
import { uploadFile } from '@/api/user'
import SkeletonScreen from '@/components/SkeletonScreen.vue'
import type { Dish, DishType, DishPackage } from '@/types/dish'

// 菜品分类列表
const dishTypeList = ref<DishType[]>([])

// 搜索表单
const searchForm = reactive({
  name: '',
  typeId: null as number | null,
  status: null as number | null
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref<Dish[]>([])
const loading = ref(false)

// 菜品对话框
const dishDialogVisible = ref(false)
const dishDialogType = ref<'add' | 'edit'>('add')
const submitLoading = ref(false)
const dishFormRef = ref<FormInstance>()

// 菜品表单
const dishForm = reactive({
  id: 0,
  name: '',
  typeId: null as number | null,
  detail: '',
  coverUrl: '',
  status: true,
  dishesPackageList: [] as { name: string; specs: string; price: number }[]
})

// 菜品表单验证规则
const dishRules: FormRules = {
  name: [
    { required: true, message: '请输入菜品名称', trigger: 'blur' }
  ],
  typeId: [
    { required: true, message: '请选择所属分类', trigger: 'change' }
  ],
  coverUrl: [
    { required: true, message: '请上传封面图', trigger: 'change' }
  ],
  detail: [
    { required: true, message: '请输入菜品介绍', trigger: 'blur' }
  ]
}

// 套餐管理对话框
const packageDialogVisible = ref(false)
const packageLoading = ref(false)
const currentDish = ref<Dish | null>(null)
const packageList = ref<DishPackage[]>([])

// 套餐编辑对话框
const packageEditVisible = ref(false)
const packageEditType = ref<'add' | 'edit'>('add')
const packageSubmitLoading = ref(false)
const packageFormRef = ref<FormInstance>()
const packageForm = reactive({
  id: 0,
  dishesId: 0,
  name: '',
  specs: '',
  price: 0.01
})

// 套餐表单验证规则
const packageRules: FormRules = {
  name: [
    { required: true, message: '请输入套餐名', trigger: 'blur' }
  ],
  specs: [
    { required: true, message: '请输入规格', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '价格必须大于0', trigger: 'blur' }
  ]
}

// 加载菜品分类
const loadDishTypes = async () => {
  try {
    const res = await fetchDishTypeList({ current: 0, size: 100 })
    if (res.code === 200) {
      dishTypeList.value = res.data || []
    }
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

// 加载菜品数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getDishList({
      current: pagination.current - 1,
      size: pagination.size,
      name: searchForm.name || undefined,
      typeId: searchForm.typeId,
      status: searchForm.status
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
  searchForm.typeId = null
  searchForm.status = null
  handleSearch()
}

// 新增菜品
const handleAdd = () => {
  dishDialogType.value = 'add'
  dishForm.id = 0
  dishForm.name = ''
  dishForm.typeId = null
  dishForm.detail = ''
  dishForm.coverUrl = ''
  dishForm.status = true
  dishForm.dishesPackageList = []
  dishDialogVisible.value = true
}

// 编辑菜品
const handleEdit = (row: Dish) => {
  dishDialogType.value = 'edit'
  dishForm.id = row.id
  dishForm.name = row.name
  dishForm.typeId = row.typeId
  dishForm.detail = row.detail
  dishForm.coverUrl = row.coverUrl
  dishForm.status = row.status
  dishForm.dishesPackageList = row.dishesPackageList?.map(pkg => ({
    name: pkg.name,
    specs: pkg.specs,
    price: pkg.price
  })) || []
  dishDialogVisible.value = true
}

// 删除菜品
const handleDelete = (row: Dish) => {
  ElMessageBox.confirm(
    `确定要删除菜品"${row.name}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteDish(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error: any) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

// 切换菜品状态
const handleToggleStatus = (row: Dish) => {
  const newStatus = !row.status
  const action = newStatus ? '上架' : '下架'
  ElMessageBox.confirm(
    `确定要${action}菜品"${row.name}"吗？`,
    `${action}确认`,
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await updateDishStatus({ id: row.id, status: newStatus })
      if (res.code === 200) {
        ElMessage.success(`${action}成功`)
        loadData()
      } else {
        ElMessage.error(res.message || `${action}失败`)
      }
    } catch (error: any) {
      ElMessage.error(error.message || `${action}失败`)
    }
  }).catch(() => {})
}

// 封面上传前验证
const beforeCoverUpload = (file: File) => {
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

// 封面上传
const handleCoverUpload = async (options: UploadRequestOptions) => {
  try {
    const res = await uploadFile(options.file)
    if (res.code === 200) {
      dishForm.coverUrl = res.data
      ElMessage.success('封面上传成功')
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '上传失败')
  }
}

// 添加套餐
const addPackage = () => {
  dishForm.dishesPackageList.push({ name: '', specs: '', price: 0.01 })
}

// 移除套餐
const removePackage = (index: number) => {
  dishForm.dishesPackageList.splice(index, 1)
}

// 提交菜品表单
const handleDishSubmit = async () => {
  if (!dishFormRef.value) return
  await dishFormRef.value.validate(async (valid) => {
    if (valid) {
      // 验证套餐
      if (dishForm.dishesPackageList.length === 0) {
        ElMessage.warning('请至少添加一个套餐')
        return
      }
      for (const pkg of dishForm.dishesPackageList) {
        if (!pkg.name || !pkg.specs || pkg.price <= 0) {
          ElMessage.warning('请完善套餐信息')
          return
        }
      }

      submitLoading.value = true
      try {
        if (dishDialogType.value === 'add') {
          const res = await saveDish({
            dishes: {
              typeId: dishForm.typeId!,
              name: dishForm.name,
              detail: dishForm.detail,
              coverUrl: dishForm.coverUrl,
              status: dishForm.status
            },
            dishesPackageList: dishForm.dishesPackageList
          })
          if (res.code === 200) {
            ElMessage.success('新增成功')
            dishDialogVisible.value = false
            loadData()
          } else {
            ElMessage.error(res.message || '新增失败')
          }
        } else {
          const res = await updateDish({
            id: dishForm.id,
            typeId: dishForm.typeId!,
            name: dishForm.name,
            detail: dishForm.detail,
            coverUrl: dishForm.coverUrl,
            status: dishForm.status
          })
          if (res.code === 200) {
            ElMessage.success('修改成功')
            dishDialogVisible.value = false
            loadData()
          } else {
            ElMessage.error(res.message || '修改失败')
          }
        }
      } catch (error: any) {
        ElMessage.error(error.message || '操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 查看套餐
const handleViewPackages = async (row: Dish) => {
  currentDish.value = row
  packageDialogVisible.value = true
  await loadPackages(row.id)
}

// 加载套餐列表
const loadPackages = async (dishesId: number) => {
  packageLoading.value = true
  try {
    const res = await getDishPackageList({ current: 0, size: 100, dishesId })
    if (res.code === 200) {
      packageList.value = res.data || []
    } else {
      ElMessage.error(res.message || '查询套餐失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '查询套餐失败')
  } finally {
    packageLoading.value = false
  }
}

// 新增套餐
const handleAddPackage = () => {
  packageEditType.value = 'add'
  packageForm.id = 0
  packageForm.dishesId = currentDish.value?.id || 0
  packageForm.name = ''
  packageForm.specs = ''
  packageForm.price = 0.01
  packageEditVisible.value = true
}

// 编辑套餐
const handleEditPackage = (row: DishPackage) => {
  packageEditType.value = 'edit'
  packageForm.id = row.id || 0
  packageForm.dishesId = row.dishesId || currentDish.value?.id || 0
  packageForm.name = row.name
  packageForm.specs = row.specs
  packageForm.price = row.price
  packageEditVisible.value = true
}

// 删除套餐
const handleDeletePackage = (row: DishPackage) => {
  ElMessageBox.confirm(
    `确定要删除套餐"${row.name}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteDishPackage(row.id!)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        if (currentDish.value) {
          await loadPackages(currentDish.value.id)
        }
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error: any) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

// 提交套餐表单
const handlePackageSubmit = async () => {
  if (!packageFormRef.value) return
  await packageFormRef.value.validate(async (valid) => {
    if (valid) {
      packageSubmitLoading.value = true
      try {
        const api = packageEditType.value === 'add' ? saveDishPackage : updateDishPackage
        const res = await api({
          id: packageEditType.value === 'edit' ? packageForm.id : undefined,
          dishesId: packageForm.dishesId,
          name: packageForm.name,
          specs: packageForm.specs,
          price: packageForm.price
        })
        if (res.code === 200) {
          ElMessage.success(packageEditType.value === 'add' ? '新增成功' : '修改成功')
          packageEditVisible.value = false
          if (currentDish.value) {
            await loadPackages(currentDish.value.id)
          }
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error: any) {
        ElMessage.error(error.message || '操作失败')
      } finally {
        packageSubmitLoading.value = false
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
  loadDishTypes()
  loadData()
})
</script>

<style scoped lang="scss">
.dish-management {
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

.search-area :deep(.el-input),
.search-area :deep(.el-select) {
  width: min(220px, 42vw);
}

.dish-cover {
  width: 52px;
  height: 52px;
  overflow: hidden;
  border: 1px solid #f0e4da;
  border-radius: 10px;
  box-shadow: 0 3px 10px rgba(65, 42, 28, 0.1);
}

.cover-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

.preview-cover {
  width: 100px;
  height: 100px;
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

.package-list {
  .package-item {
    margin-bottom: 12px;
    padding: 14px 14px 2px;
    border: 1px solid #f0e5dc;
    border-radius: 12px;
    background: #fffaf7;
  }

  .add-package-btn {
    margin-top: 10px;
  }
}

.package-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  color: var(--ink-900);
  font-size: 16px;
  font-weight: 700;
}

.price {
  color: var(--brand-600);
  font-weight: 750;
  font-variant-numeric: tabular-nums;
}

@media (max-width: 640px) {
  .dish-management {
    padding-top: 18px;
  }

  .box-card :deep(.el-card__header),
  .box-card :deep(.el-card__body) {
    padding-right: 16px;
    padding-left: 16px;
  }

  .card-header {
    align-items: flex-start;
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

  .search-area :deep(.el-input),
  .search-area :deep(.el-select) {
    width: 100%;
  }

  .pagination {
    justify-content: center;
  }
}
</style>
