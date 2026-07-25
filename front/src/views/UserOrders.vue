<template>
  <div class="user-orders">
    <h1>我的订单</h1>
    <el-tabs v-model="selectedStatus" @tab-change="handleStatusChange">
      <el-tab-pane label="全部" :name="null" />
      <el-tab-pane v-for="status in orderStatuses" :key="status.value" :label="status.label" :name="status.value" />
    </el-tabs>

    <SkeletonScreen v-if="loading" variant="list" :count="4" />
    <el-empty v-else-if="orderList.length === 0" description="暂无订单" />
    <template v-else>
    <el-card v-for="order in orderList" :key="order.id" shadow="hover" class="order-card" :class="`status-${order.status}`">
      <div class="order-header">
        <span>订单号：{{ order.code }}</span>
        <el-tag :type="statusTagType(order.status)">{{ statusLabel(order.status) }}</el-tag>
      </div>
      <el-descriptions :column="2" size="small" class="order-info">
        <el-descriptions-item label="餐桌">{{ order.dishesTableNumber || `餐桌 #${order.dishesTableId}` }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ order.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付时间" v-if="order.payTime">{{ order.payTime }}</el-descriptions-item>
      </el-descriptions>
      <div class="order-footer">
        <span>合计：<strong>¥{{ formatMoney(order.totalPrice) }}</strong></span>
        <el-space>
          <el-button type="primary" link @click="showDetail(order)">查看详情</el-button>
          <el-tag v-if="refundStatusByOrder[order.id]" :type="refundTagType(refundStatusByOrder[order.id])">
            {{ refundStatusLabel(refundStatusByOrder[order.id]) }}
          </el-tag>
          <el-button v-else-if="order.status === 2" type="warning" link @click="openRefund(order)">申请退款</el-button>
          <OrderStatusActions :order="order" is-user @updated="loadOrders" />
        </el-space>
      </div>
    </el-card>
    </template>

    <div v-if="total > pageSize" class="pagination">
      <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="loadOrders" />
    </div>
    <OrderDetailDialog v-model="detailVisible" :order="currentOrder" />
    <el-dialog v-model="refundVisible" title="申请退款" width="460px">
      <el-input v-model="refundCause" type="textarea" :rows="4" maxlength="200" show-word-limit placeholder="请填写退款原因" />
      <template #footer><el-button @click="refundVisible=false">取消</el-button><el-button type="primary" :loading="refundSubmitting" @click="submitRefund">提交申请</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserOrderList } from '@/api/order'
import { applyRefund, getRefundList } from '@/api/refund'
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

const orderList = ref<Order[]>([])
const loading = ref(false)
const selectedStatus = ref<number | null>(null)
const currentPage = ref(1)
const pageSize = 10
const total = ref(0)
const detailVisible = ref(false)
const currentOrder = ref<Order | null>(null)
const refundVisible = ref(false)
const refundSubmitting = ref(false)
const refundCause = ref('')
const refundOrder = ref<Order | null>(null)
const refundStatusByOrder = ref<Record<number, number>>({})

const statusLabel = (status: number) => orderStatuses.find(item => item.value === status)?.label || '未知状态'
const statusTagType = (status: number) => ({ 1: 'warning', 2: 'primary', 3: 'success', 4: 'info', 5: 'danger' }[status] || 'info') as 'warning' | 'primary' | 'success' | 'info' | 'danger'
const formatMoney = (value: number) => Number(value || 0).toFixed(2)

const handleStatusChange = () => {
  currentPage.value = 1
  loadOrders()
}
const refundStatusLabel = (status: number) => ({ 1: '退款审核中', 2: '已退款', 3: '退款已拒绝' }[status] || '退款处理中')
const refundTagType = (status: number) => ({ 1: 'warning', 2: 'success', 3: 'danger' }[status] || 'info') as 'warning' | 'success' | 'danger' | 'info'
const showDetail = (order: Order) => {
  currentOrder.value = order
  detailVisible.value = true
}
const openRefund = (order: Order) => { refundOrder.value = order; refundCause.value = ''; refundVisible.value = true }
const submitRefund = async () => {
  if (!refundOrder.value || !refundCause.value.trim()) { ElMessage.warning('请填写退款原因'); return }
  refundSubmitting.value = true
  try {
    await applyRefund(refundOrder.value.id, refundCause.value.trim())
    ElMessage.success('退款申请已提交')
    refundVisible.value = false
    await loadOrders()
  } finally { refundSubmitting.value = false }
}

const loadOrders = async () => {
  loading.value = true
  try {
    const [ordersRes, refundsRes] = await Promise.all([
      getUserOrderList({ current: currentPage.value - 1, size: pageSize, status: selectedStatus.value, code: '' }),
      getRefundList()
    ])
    orderList.value = ordersRes.data || []
    total.value = Number(ordersRes.count || 0)
    refundStatusByOrder.value = Object.fromEntries((refundsRes.data || []).map(item => [item.ordersId, item.status]))
  } finally {
    loading.value = false
  }
}

onMounted(loadOrders)
</script>

<style scoped lang="scss">
.user-orders { max-width: 960px; margin: 0 auto; padding: 34px 20px 44px; }
h1 { margin: 0 0 16px; color: #30241f; font-size: 28px; font-weight: 750; }
.order-card { position: relative; overflow: hidden; margin-bottom: 16px; border-radius: 16px; transition: transform .2s ease, box-shadow .2s ease; &:hover { transform: translateY(-2px); box-shadow: 0 12px 28px rgba(65,42,28,.1); }
  &::before { position: absolute; top: 0; bottom: 0; left: 0; width: 4px; background: transparent; content: ''; }
  &.status-1::before { background: var(--gradient-gold); }
  &.status-2::before { background: var(--gradient-brand); }
  &.status-3::before { background: var(--gradient-jade); }
  &.status-4::before { background: #b6ada6; }
  &.status-5::before { background: #d98a8a; }
}
.order-header, .order-footer { display: flex; justify-content: space-between; align-items: center; gap: 16px; }
.order-header { padding-bottom: 14px; color: #4d413b; border-bottom: 1px solid #f0e6df; font-weight: 700; }
.order-info { margin: 18px 0; }
.order-footer strong { color: #c9502d; font-size: 20px; font-variant-numeric: tabular-nums; }
.pagination { display: flex; justify-content: center; margin-top: 28px; }
@media (max-width: 700px) { .user-orders { padding: 22px 14px 34px; } h1 { font-size: 24px; } .order-header, .order-footer { align-items: flex-start; flex-direction: column; gap: 10px; } }
</style>
