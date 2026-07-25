<template>
  <div class="order-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="card-header-text">
            <span class="card-title">订单管理</span>
            <span class="card-subtitle">查询、处理与跟踪全部订单</span>
          </div>
        </div>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.code" placeholder="请输入订单号" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable class="status-select">
            <el-option v-for="status in orderStatuses" :key="status.value" :label="status.label" :value="status.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-tabs v-model="searchForm.status" @tab-change="handleStatusChange">
        <el-tab-pane label="全部" :name="null" />
        <el-tab-pane v-for="status in orderStatuses" :key="status.value" :label="status.label" :name="status.value" />
      </el-tabs>

      <SkeletonScreen v-if="loading" variant="table" :count="6" />
      <el-table v-else :data="orderList" border stripe>
        <el-table-column prop="code" label="订单号" min-width="155" />
        <el-table-column label="用户" min-width="110">
          <template #default="{ row }">{{ row.username || `用户 #${row.userId}` }}</template>
        </el-table-column>
        <el-table-column label="餐桌" min-width="100">
          <template #default="{ row }">{{ row.dishesTableNumber || `餐桌 #${row.dishesTableId}` }}</template>
        </el-table-column>
        <el-table-column label="金额" width="110" align="right">
          <template #default="{ row }"><span class="price">¥{{ formatMoney(row.totalPrice) }}</span></template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }"><el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="showDetail(row)">查看详情</el-button>
            <OrderStatusActions :order="row" @updated="loadOrders" />
            <el-button v-if="row.status === 1 || row.status === 5" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && orderList.length === 0" description="暂无订单" />
      <div class="pagination" v-if="pagination.total > 0">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="loadOrders"
        />
      </div>
    </el-card>
    <OrderDetailDialog v-model="detailVisible" :order="currentOrder" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteOrder, getOrderList } from '@/api/order'
import OrderStatusActions from '@/components/OrderStatusActions.vue'
import OrderDetailDialog from '@/components/OrderDetailDialog.vue'
import SkeletonScreen from '@/components/SkeletonScreen.vue'
import type { Order } from '@/types/order'

const orderStatuses = [
  { value: 1, label: '待支付' },
  { value: 2, label: '已支付' },
  { value: 3, label: '出餐中' },
  { value: 4, label: '已完成' },
  { value: 5, label: '已取消' }
]
const searchForm = reactive({ code: '', status: null as number | null })
const pagination = reactive({ current: 1, size: 10, total: 0 })
const orderList = ref<Order[]>([])
const loading = ref(false)
const detailVisible = ref(false)
const currentOrder = ref<Order | null>(null)

const statusLabel = (status: number) => orderStatuses.find(item => item.value === status)?.label || '未知状态'
const statusTagType = (status: number) => ({ 1: 'warning', 2: 'primary', 3: 'success', 4: 'info', 5: 'danger' }[status] || 'info') as 'warning' | 'primary' | 'success' | 'info' | 'danger'
const formatMoney = (value: number) => Number(value || 0).toFixed(2)

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await getOrderList({
      current: pagination.current - 1,
      size: pagination.size,
      status: searchForm.status,
      code: searchForm.code
    })
    orderList.value = res.data || []
    pagination.total = res.count || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadOrders()
}
const handleReset = () => {
  searchForm.code = ''
  searchForm.status = null
  handleSearch()
}
const handleStatusChange = () => handleSearch()
const handleSizeChange = () => {
  pagination.current = 1
  loadOrders()
}
const showDetail = (order: Order) => {
  currentOrder.value = order
  detailVisible.value = true
}
const handleDelete = async (order: Order) => {
  try {
    await ElMessageBox.confirm(`确认删除订单“${order.code}”吗？`, '删除确认', {
      confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning'
    })
    await deleteOrder(order.id)
    ElMessage.success('删除成功')
    if (orderList.value.length === 1 && pagination.current > 1) pagination.current -= 1
    await loadOrders()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') console.error('删除订单失败', error)
  }
}

onMounted(loadOrders)
</script>

<style scoped lang="scss">
.order-management {
  padding: 28px 0 8px;
}

.order-management :deep(.el-card__header) {
  padding: 20px 24px;
  font-size: 20px;
  letter-spacing: -0.02em;
}

.order-management :deep(.el-card__body) {
  padding: 22px 24px 24px;
}

.search-form {
  display: flex;
  gap: 0 4px;
  align-items: flex-end;
  margin-bottom: 18px;
}

.search-form :deep(.el-input) {
  width: min(260px, 56vw);
}

.status-select {
  width: 132px;
}

.order-management :deep(.el-tabs__header) {
  margin: 0 0 18px;
}

.order-management :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: var(--line);
}

.price {
  color: var(--brand-600);
  font-weight: 750;
  font-variant-numeric: tabular-nums;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 22px;
  padding-top: 18px;
  border-top: 1px solid var(--line);
}

@media (max-width: 640px) {
  .order-management {
    padding-top: 18px;
  }

  .order-management :deep(.el-card__header),
  .order-management :deep(.el-card__body) {
    padding-right: 16px;
    padding-left: 16px;
  }

  .search-form {
    display: block;
  }

  .search-form :deep(.el-form-item) {
    display: flex;
    width: 100%;
    margin: 0 0 12px;
  }

  .search-form :deep(.el-form-item__content) {
    flex: 1;
  }

  .search-form :deep(.el-input),
  .status-select {
    width: 100%;
  }

  .pagination {
    justify-content: center;
  }
}
</style>
