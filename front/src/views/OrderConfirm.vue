<template>
  <div class="order-confirm">
    <div class="page-heading">
      <el-button text @click="router.back()">← 返回</el-button>
      <h1>确认订单</h1>
    </div>

    <el-steps :active="1" align-center finish-status="success" class="order-steps">
      <el-step title="购物车" />
      <el-step title="确认订单" />
      <el-step title="下单完成" />
    </el-steps>

    <SkeletonScreen v-if="loading" variant="list" :count="2" />

    <template v-else>
    <el-alert
      v-if="loadWarning"
      :title="loadWarning"
      type="warning"
      :closable="false"
      show-icon
      class="load-warning"
    />

    <el-card class="section-card">
      <template #header>餐桌选择</template>
      <el-select v-model="dishesTableId" placeholder="请选择就餐餐桌" class="table-select">
        <el-option
          v-for="table in tableList"
          :key="table.id"
          :label="`${table.number}（${table.personNumber} 人）`"
          :value="table.id"
          :disabled="!table.status || table.occupied"
        />
      </el-select>
      <p v-if="tableList.length === 0" class="hint">暂无可选餐桌，请稍后重试。</p>
    </el-card>

    <el-card class="section-card">
      <template #header>订单商品</template>
      <el-empty v-if="cartItems.length === 0" description="暂无已选商品，请先在购物车中选择商品" />
      <div v-else class="order-items">
        <div v-for="item in cartItems" :key="item.id" class="order-item">
          <el-image v-lazy lazy :src="item.dishesCover" fit="cover" class="item-cover">
            <template #error><div class="image-placeholder">暂无图片</div></template>
          </el-image>
          <div class="item-info">
            <strong>{{ item.dishesName }} - {{ item.dishesPackageName }}</strong>
            <span>¥{{ formatMoney(item.dishesPackagePrice) }} × {{ item.plusNumber }}</span>
          </div>
          <span class="item-total">¥{{ formatMoney(item.dishesPackagePrice * item.plusNumber) }}</span>
        </div>
      </div>
      <div class="summary-row">商品合计 <strong>¥{{ formatMoney(totalPrice) }}</strong></div>
      <div class="wallet-row">订单创建后可在“我的订单”中支付</div>
    </el-card>

    </template>

    <div class="submit-bar">
      <span>合计：<strong>¥{{ formatMoney(totalPrice) }}</strong></span>
      <el-button type="primary" :loading="submitting" :disabled="cartItems.length === 0" @click="submitOrder">
        提交订单
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createOrder, getAvailableDishesTables, getSelectedCartItems } from '@/api/order'
import type { DishesTable, ShippingCarItem } from '@/types/order'
import SkeletonScreen from '@/components/SkeletonScreen.vue'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const dishesTableId = ref<number | null>(null)
const tableList = ref<DishesTable[]>([])
const cartItems = ref<ShippingCarItem[]>([])
const loadWarning = ref('')

const totalPrice = computed(() => cartItems.value.reduce(
  (sum, item) => sum + item.dishesPackagePrice * item.plusNumber,
  0
))

const formatMoney = (value: number) => Number(value || 0).toFixed(2)

const loadConfirmationData = async () => {
  loading.value = true
  const failedFeatures: string[] = []
  const [tablesResult, cartResult] = await Promise.allSettled([
    getAvailableDishesTables(),
    getSelectedCartItems()
  ])

  if (tablesResult.status === 'fulfilled') {
    tableList.value = (tablesResult.value.data || []).filter(item => item.status && !item.occupied)
  } else {
    failedFeatures.push('餐桌')
  }
  if (cartResult.status === 'fulfilled') {
    cartItems.value = cartResult.value.data || []
  } else {
    failedFeatures.push('购物车')
  }
  if (failedFeatures.length > 0) {
    loadWarning.value = `${failedFeatures.join('、')}数据暂不可用；相关模块完成后可正常显示。`
  }
  loading.value = false
}

const submitOrder = async () => {
  if (!dishesTableId.value) {
    ElMessage.warning('请选择就餐餐桌')
    return
  }
  if (cartItems.value.length === 0) {
    ElMessage.warning('订单商品不能为空')
    return
  }

  submitting.value = true
  try {
    await createOrder({
      dishesTableId: dishesTableId.value,
      orderItemDtoList: cartItems.value.map(item => ({
        dishesPackageId: item.dishesPackageId,
        buyNumber: item.plusNumber
      }))
    })
    ElMessage.success('下单成功，请及时支付')
    router.replace('/orders')
  } finally {
    submitting.value = false
  }
}

onMounted(loadConfirmationData)
</script>

<style scoped lang="scss">
.order-confirm { max-width: 960px; margin: 0 auto; padding: 34px 20px 118px; }
.page-heading { display: flex; align-items: center; gap: 14px; margin-bottom: 10px; h1 { margin: 0; color: #30241f; font-size: 28px; font-weight: 750; } }
.order-steps { max-width: 560px; margin: 0 auto 26px; }
.load-warning, .section-card { margin-bottom: 16px; }
.section-card { border-radius: 16px; }
.table-select { width: 100%; max-width: 460px; }
.hint, .wallet-row { color: #887d75; font-size: 14px; }
.order-item { display: flex; align-items: center; gap: 16px; padding: 15px 0; border-bottom: 1px solid #f0e6df; }
.item-cover { width: 72px; height: 72px; border: 1px solid #f0e3db; border-radius: 12px; flex: none; }
.image-placeholder { width: 100%; height: 100%; display: grid; place-items: center; color: #9a7869; background: #fff0e8; font-size: 12px; }
.item-info { display: flex; flex: 1; flex-direction: column; gap: 8px; color: #796f68; strong { color: #352923; font-size: 16px; } }
.item-total, .summary-row strong, .submit-bar strong { color: #c9502d; font-size: 19px; font-variant-numeric: tabular-nums; }
.summary-row { display: flex; justify-content: flex-end; gap: 16px; padding: 20px 0 8px; }
.submit-bar { position: fixed; right: max(16px, calc((100% - 960px) / 2)); bottom: 18px; left: max(16px, calc((100% - 960px) / 2)); z-index: 10; display: flex; align-items: center; justify-content: flex-end; gap: 24px; padding: 15px 20px; border: 1px solid rgba(238,218,207,.95); border-radius: 16px; background: rgba(255,253,251,.94); box-shadow: 0 12px 32px rgba(65,42,28,.14); backdrop-filter: blur(14px); }
@media (max-width: 700px) { .order-confirm { padding: 22px 14px 132px; } .page-heading h1 { font-size: 24px; } .submit-bar { right: 12px; bottom: 12px; left: 12px; flex-wrap: wrap; justify-content: space-between; gap: 12px; padding: 13px 16px; } }
</style>
