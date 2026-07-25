<template>
  <el-dialog v-model="visible" title="订单详情" width="760px" @closed="itemList = []">
    <template v-if="order">
      <el-descriptions title="订单基本信息" :column="2" border size="small" class="order-summary">
        <el-descriptions-item label="订单号">{{ order.code }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">{{ statusLabel(order.status) }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ order.username || `用户 #${order.userId}` }}</el-descriptions-item>
        <el-descriptions-item label="餐桌">{{ order.dishesTableNumber || `餐桌 #${order.dishesTableId}` }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ order.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ order.payTime || '-' }}</el-descriptions-item>
      </el-descriptions>

      <h3>订单商品</h3>
      <el-table :data="itemList" v-loading="loading" border>
        <el-table-column label="商品" min-width="235">
          <template #default="{ row }">
            <div class="dish-cell">
              <el-image v-lazy lazy :src="row.snapCover" fit="cover" class="cover">
                <template #error><div class="cover-placeholder">暂无图片</div></template>
              </el-image>
              <span>{{ row.snapName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="110" align="right">
          <template #default="{ row }">¥{{ formatMoney(row.snapPrice) }}</template>
        </el-table-column>
        <el-table-column prop="buyNumber" label="数量" width="80" align="center" />
        <el-table-column label="小计" width="120" align="right">
          <template #default="{ row }"><span class="price">¥{{ formatMoney(row.totalPrice) }}</span></template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && itemList.length === 0" description="暂无订单商品" />
      <div class="total">合计：<strong>¥{{ formatMoney(order.totalPrice) }}</strong></div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { getOrderItemList } from '@/api/order'
import type { Order, OrderItem } from '@/types/order'

const visible = defineModel<boolean>({ required: true })
const props = defineProps<{ order: Order | null }>()
const itemList = ref<OrderItem[]>([])
const loading = ref(false)
const statusLabel = (status: number) => ({ 1: '待支付', 2: '已支付', 3: '出餐中', 4: '已完成', 5: '已取消' }[status] || '未知状态')
const formatMoney = (value: number) => Number(value || 0).toFixed(2)

const loadOrderItems = async () => {
  if (!visible.value || !props.order) return
  loading.value = true
  try {
    const res = await getOrderItemList(props.order.id)
    itemList.value = res.data || []
  } catch {
    itemList.value = []
  } finally {
    loading.value = false
  }
}

watch(() => [visible.value, props.order?.id], loadOrderItems)
</script>

<style scoped lang="scss">
.order-summary { margin-bottom: 24px; overflow: hidden; border-radius: 12px; }
h3 { margin: 0 0 14px; color: #3d302a; font-size: 17px; font-weight: 750; }
.dish-cell { display: flex; align-items: center; gap: 12px; color: #443832; font-weight: 600; }
.cover { width: 50px; height: 50px; flex: none; border: 1px solid #f0e3db; border-radius: 10px; }
.cover-placeholder { width: 100%; height: 100%; display: grid; place-items: center; background: #fff0e8; color: #9a7869; font-size: 11px; }
.price, .total strong { color: #c9502d; font-size: 18px; font-variant-numeric: tabular-nums; }
.total { margin-top: 20px; padding-top: 16px; border-top: 1px dashed #eaded5; color: #6a5c54; font-size: 15px; text-align: right; }
</style>
