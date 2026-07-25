<template>
  <div class="wallet-page">
    <h1>我的钱包</h1>
    <SkeletonScreen v-if="loading && !wallet" variant="list" :count="2" />
    <el-card v-if="wallet" class="wallet-card" shadow="never">
      <div class="wallet-label">可用余额</div>
      <div class="wallet-balance">¥{{ surplusDisplay }}</div>
      <el-tag :type="wallet.status ? 'success' : 'danger'">
        {{ wallet.status ? '正常' : '已冻结' }}
      </el-tag>
    </el-card>
    <el-card v-if="wallet" class="recharge-card" shadow="never">
      <template #header>模拟充值</template>
      <el-form :inline="true" @submit.prevent="submitRecharge">
        <el-form-item label="充值金额">
          <el-input-number v-model="rechargeMoney" :min="1" :precision="0" :step="1" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="recharging" :disabled="!wallet.status" native-type="submit">充值</el-button>
        </el-form-item>
      </el-form>
      <el-text v-if="!wallet.status" type="danger">钱包已冻结，无法充值</el-text>
    </el-card>
    <el-card v-if="wallet" class="records-card" shadow="never">
      <template #header>交易记录</template>
      <el-empty v-if="walletInfos.length === 0" description="暂无交易记录" :image-size="72" />
      <el-table v-else :data="walletInfos" stripe>
        <el-table-column prop="detail" label="交易描述" min-width="180" />
        <el-table-column label="金额" width="130" align="right">
          <template #default="{ row }">
            <span :class="row.surplusMoney >= 0 ? 'income' : 'expense'">
              {{ row.surplusMoney >= 0 ? '+' : '' }}¥{{ formatMoney(row.surplusMoney) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="交易时间" min-width="170" />
      </el-table>
    </el-card>
    <el-empty v-else-if="!loading" description="钱包信息暂不可用" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getWalletDetail, getWalletInfoList, rechargeWallet } from '@/api/wallet'
import type { Wallet, WalletInfo } from '@/types/wallet'
import SkeletonScreen from '@/components/SkeletonScreen.vue'
import { useCountUp } from '@/composables/useCountUp'

const loading = ref(false)
const wallet = ref<Wallet | null>(null)
const walletInfos = ref<WalletInfo[]>([])
const rechargeMoney = ref(100)
const recharging = ref(false)

const surplusValue = computed(() => Number(wallet.value?.surplus || 0))
const surplusDisplay = useCountUp(surplusValue, { decimals: 2 })

const formatMoney = (value: number) => Number(value || 0).toFixed(2)

const loadWallet = async () => {
  loading.value = true
  try {
    const res = await getWalletDetail()
    wallet.value = res.data || null
    if (wallet.value) {
      await loadWalletInfos(wallet.value.id)
    }
  } finally {
    loading.value = false
  }
}

const loadWalletInfos = async (walletId: number) => {
  try {
    const res = await getWalletInfoList(walletId)
    walletInfos.value = res.data || []
  } catch { /* 统一响应拦截器已提示 */ }
}

const submitRecharge = async () => {
  if (!Number.isInteger(rechargeMoney.value) || rechargeMoney.value <= 0) {
    ElMessage.warning('充值金额必须为正整数')
    return
  }
  recharging.value = true
  try {
    await rechargeWallet(rechargeMoney.value)
    ElMessage.success('充值成功')
    await loadWallet()
  } finally {
    recharging.value = false
  }
}

onMounted(loadWallet)
</script>

<style scoped lang="scss">
.wallet-page { max-width: 760px; margin: 0 auto; padding: 34px 20px 44px; }
h1 { margin: 0 0 20px; color: #30241f; font-size: 28px; font-weight: 750; }
.wallet-card { position: relative; overflow: hidden; padding: 30px; color: #fff; text-align: left; border: 0; background: linear-gradient(125deg, #47231d 0%, #923a28 58%, #d46238 100%); box-shadow: 0 17px 34px rgba(117, 46, 28, .22); &::after { position: absolute; width: 230px; height: 230px; right: -78px; bottom: -126px; border: 1px solid rgba(255,255,255,.24); border-radius: 50%; box-shadow: 0 0 0 30px rgba(255,255,255,.06), 0 0 0 65px rgba(255,255,255,.04); content: ''; } :deep(.el-card__body) { position: relative; z-index: 1; padding: 0; } :deep(.el-tag) { border-color: rgba(255,255,255,.25); background: rgba(255,255,255,.14); color: #fff; } }
.recharge-card, .records-card { margin-top: 18px; }
.income { color: #23836b; }
.expense { color: #c94848; }
.wallet-label { color: rgba(255,255,255,.78); font-size: 14px; }
.wallet-balance { margin: 8px 0 20px; color: #fff; font-size: 46px; font-weight: 750; letter-spacing: -.02em; font-variant-numeric: tabular-nums; }
@media (max-width: 700px) { .wallet-page { padding: 22px 14px 34px; } h1 { font-size: 24px; } .wallet-card { padding: 24px; } .wallet-balance { font-size: 38px; } }
</style>
