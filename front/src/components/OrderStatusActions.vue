<template>
  <el-space wrap>
    <el-button
      v-if="isUser && order.status === 1"
      type="primary"
      size="small"
      :loading="workingAction === 'pay'"
      @click="runAction('pay')"
    >去支付</el-button>
    <el-button
      v-if="isUser && order.status === 1"
      type="danger"
      plain
      size="small"
      :loading="workingAction === 'cancel'"
      @click="runAction('cancel')"
    >取消订单</el-button>
    <el-button
      v-if="!isUser && order.status === 2"
      type="primary"
      size="small"
      :loading="workingAction === 'serve'"
      @click="runAction('serve')"
    >接单出餐</el-button>
    <el-button
      v-if="!isUser && order.status === 3"
      type="success"
      size="small"
      :loading="workingAction === 'complete'"
      @click="runAction('complete')"
    >完成订单</el-button>
  </el-space>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelOrder, completeOrder, payOrder, serveOrder } from '@/api/order'
import type { Order } from '@/types/order'
import { refreshHeaderIndicators } from '@/utils/headerIndicator'

type Action = 'pay' | 'cancel' | 'serve' | 'complete'

const props = withDefaults(defineProps<{
  order: Order
  isUser?: boolean
}>(), {
  isUser: false
})

const emit = defineEmits<{ updated: [] }>()
const workingAction = ref<Action | null>(null)

const actionConfig: Record<Action, { text: string; success: string; type: 'warning' | 'info' }> = {
  pay: { text: '确认使用钱包余额支付此订单吗？', success: '支付成功', type: 'info' },
  cancel: { text: '确认取消该待支付订单吗？', success: '订单已取消', type: 'warning' },
  serve: { text: '确认接单并开始出餐吗？', success: '已接单，订单进入出餐中', type: 'info' },
  complete: { text: '确认将订单标记为已完成吗？', success: '订单已完成', type: 'info' }
}

const actionApi: Record<Action, (ordersId: number) => Promise<unknown>> = {
  pay: payOrder,
  cancel: cancelOrder,
  serve: serveOrder,
  complete: completeOrder
}

const runAction = async (action: Action) => {
  const config = actionConfig[action]
  try {
    await ElMessageBox.confirm(config.text, '操作确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: config.type
    })
    workingAction.value = action
    await actionApi[action](props.order.id)
    ElMessage.success(config.success)
    refreshHeaderIndicators()
    emit('updated')
  } catch (error) {
    // Element Plus 的取消操作不需要提示错误；请求失败已由统一响应拦截器提示。
    if (error !== 'cancel' && error !== 'close') {
      console.error('订单状态操作失败', error)
    }
  } finally {
    workingAction.value = null
  }
}
</script>
