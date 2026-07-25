<template>
  <div class="table-management">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <div class="card-header-text">
            <span class="card-title">餐桌管理</span>
            <span class="card-subtitle">维护餐桌信息、容量与可用状态</span>
          </div>
          <el-button type="primary" @click="openAdd"><el-icon><Plus /></el-icon>新增餐桌</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="桌号"><el-input v-model="query.number" clearable placeholder="请输入桌号" @keyup.enter="search" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="query.status" clearable placeholder="全部" class="status-filter"><el-option label="可用" :value="true" /><el-option label="不可用" :value="false" /></el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="search"><el-icon><Search /></el-icon>查询</el-button><el-button @click="reset"><el-icon><Refresh /></el-icon>重置</el-button></el-form-item>
      </el-form>

      <SkeletonScreen v-if="loading" variant="table" :count="6" />
      <el-table v-else :data="tables" border stripe>
        <el-table-column label="就餐状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.occupied ? 'warning' : 'info'">{{ row.occupied ? '就餐中' : '空闲' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="number" label="桌号" min-width="150" />
        <el-table-column prop="personNumber" label="就餐人数" width="130" align="center" />
        <el-table-column label="状态" width="160" align="center"><template #default="{ row }"><el-switch :model-value="row.status" :disabled="statusUpdatingId === row.id" active-text="可用" inactive-text="不可用" @change="changeStatus(row, Boolean($event))" /></template></el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }"><el-button type="primary" link @click="openEdit(row)"><el-icon><Edit /></el-icon>编辑</el-button><el-button type="danger" link @click="remove(row)"><el-icon><Delete /></el-icon>删除</el-button></template>
        </el-table-column>
      </el-table>
      <div class="pagination"><el-pagination v-model:current-page="page" v-model:page-size="query.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="search" @current-change="loadTables" /></div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑餐桌' : '新增餐桌'" width="460px" @closed="formRef?.resetFields()">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="桌号" prop="number"><el-input v-model="form.number" maxlength="20" placeholder="例如 A01" /></el-form-item>
        <el-form-item label="就餐人数" prop="personNumber"><el-input-number v-model="form.personNumber" :min="1" :max="100" :precision="0" /></el-form-item>
        <el-form-item label="餐桌状态" prop="status"><el-radio-group v-model="form.status"><el-radio :value="true">可用</el-radio><el-radio :value="false">不可用</el-radio></el-radio-group></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" :loading="saving" @click="submit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Delete, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { deleteDishesTable, getDishesTableList, saveDishesTable, updateDishesTable } from '@/api/table'
import SkeletonScreen from '@/components/SkeletonScreen.vue'
import type { DishesTable, DishesTablePayload, DishesTableQuery } from '@/types/table'

const loading = ref(false)
const saving = ref(false)
const tables = ref<DishesTable[]>([])
const total = ref(0)
const page = ref(1)
const dialogVisible = ref(false)
const editing = ref(false)
const statusUpdatingId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const query = reactive<DishesTableQuery>({ current: 0, size: 10, number: '', status: null })
const form = reactive<DishesTablePayload>({ id: undefined, number: '', personNumber: 4, status: true })
const rules: FormRules = { number: [{ required: true, message: '请输入桌号', trigger: 'blur' }], personNumber: [{ required: true, type: 'number', min: 1, message: '就餐人数必须大于 0', trigger: 'change' }] }

const loadTables = async () => {
  loading.value = true
  try {
    const res = await getDishesTableList({ ...query, current: page.value - 1 })
    tables.value = res.data || []
    total.value = Number(res.count || 0)
  } catch (error: any) { ElMessage.error(error.message || '查询餐桌失败') } finally { loading.value = false }
}
const search = () => { page.value = 1; loadTables() }
const reset = () => { query.number = ''; query.status = null; search() }
const openAdd = () => { editing.value = false; form.id = undefined; form.number = ''; form.personNumber = 4; form.status = true; dialogVisible.value = true }
const openEdit = (row: DishesTable) => {
  if (row.occupied) { ElMessage.warning('餐桌就餐中，暂不能修改'); return }
  editing.value = true; form.id = row.id; form.number = row.number; form.personNumber = row.personNumber; form.status = row.status; dialogVisible.value = true
}
const submit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (editing.value) await updateDishesTable({ ...form })
    else await saveDishesTable({ number: form.number, personNumber: form.personNumber, status: form.status })
    ElMessage.success(editing.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    await loadTables()
  } catch (error: any) { ElMessage.error(error.message || '保存餐桌失败') } finally { saving.value = false }
}
const remove = async (row: DishesTable) => {
  try {
    await ElMessageBox.confirm(`确认删除餐桌“${row.number}”吗？`, '删除餐桌', { type: 'warning' })
    await deleteDishesTable(row.id)
    ElMessage.success('删除成功')
    await loadTables()
  } catch (error: any) { if (error !== 'cancel' && error !== 'close') ElMessage.error(error.message || '删除餐桌失败') }
}
const changeStatus = async (row: DishesTable, status: boolean) => {
  if (row.occupied) { ElMessage.warning('餐桌就餐中，暂不能修改状态'); return }
  statusUpdatingId.value = row.id
  try {
    await updateDishesTable({ id: row.id, number: row.number, personNumber: row.personNumber, status })
    row.status = status
    ElMessage.success(`餐桌已设为${status ? '可用' : '不可用'}`)
  } catch (error: any) {
    ElMessage.error(error.message || '修改餐桌状态失败')
    await loadTables()
  } finally { statusUpdatingId.value = null }
}
onMounted(loadTables)
</script>

<style scoped lang="scss">
.table-management {
  padding: 28px 0 8px;
}

.card-header {
  display: flex;
  gap: 16px;
  align-items: center;
  justify-content: space-between;
  color: var(--ink-900);
  font-size: 20px;
  font-weight: 750;
  letter-spacing: -0.02em;
}

.table-management :deep(.el-card__header) {
  padding: 18px 24px;
}

.table-management :deep(.el-card__body) {
  padding: 22px 24px 24px;
}

.search-form {
  display: flex;
  gap: 0 4px;
  align-items: flex-end;
  margin-bottom: 20px;
  padding: 16px;
  border: 1px solid #f0e6dd;
  border-radius: 12px;
  background: #fffaf6;
}

.search-form :deep(.el-input) {
  width: min(240px, 52vw);
}

.status-filter {
  width: 132px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 22px;
  padding-top: 18px;
  border-top: 1px solid var(--line);
}

@media (max-width: 640px) {
  .table-management {
    padding-top: 18px;
  }

  .table-management :deep(.el-card__header),
  .table-management :deep(.el-card__body) {
    padding-right: 16px;
    padding-left: 16px;
  }

  .card-header {
    font-size: 18px;
  }

  .search-form {
    display: block;
    padding: 12px;
  }

  .search-form :deep(.el-form-item) {
    display: flex;
    margin: 0 0 12px;
  }

  .search-form :deep(.el-form-item__content) {
    flex: 1;
  }

  .search-form :deep(.el-input),
  .status-filter {
    width: 100%;
  }

  .pagination {
    justify-content: center;
  }
}
</style>
