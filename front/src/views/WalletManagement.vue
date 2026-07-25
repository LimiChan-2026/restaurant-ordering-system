<template>
  <div class="wallet-management">
    <div class="admin-page-header">
      <div>
        <h1 class="header-title">钱包管理</h1>
        <p class="header-desc">查看平台用户钱包余额、状态与交易明细</p>
      </div>
    </div>
    <el-card shadow="never">
      <el-form :inline="true" :model="query">
        <el-form-item label="用户名"><el-input v-model="query.username" clearable placeholder="请输入用户名" @keyup.enter="search" /></el-form-item>
        <el-form-item label="钱包类型"><el-select v-model="query.type" clearable placeholder="全部"><el-option label="私人钱包" :value="1" /><el-option label="商家钱包" :value="2" /></el-select></el-form-item>
        <el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button>
      </el-form>
      <SkeletonScreen v-if="loading" variant="table" :count="5" />
      <el-table v-else :data="wallets" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户" min-width="120" />
        <el-table-column label="钱包类型" width="120"><template #default="{ row }">{{ row.type === 2 ? '商家钱包' : '私人钱包' }}</template></el-table-column>
        <el-table-column label="余额" width="140" align="right"><template #default="{ row }">¥{{ formatMoney(row.surplus) }}</template></el-table-column>
        <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status ? 'success' : 'danger'">{{ row.status ? '正常' : '冻结' }}</el-tag></template></el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="110"><template #default="{ row }"><el-button type="primary" link @click="showRecords(row)">查看明细</el-button></template></el-table-column>
      </el-table>
      <el-empty v-if="!loading && wallets.length === 0" description="暂无钱包数据" :image-size="72" />
      <div class="pagination"><el-pagination v-model:current-page="page" :page-size="query.size" :total="total" layout="prev, pager, next" @current-change="loadWallets" /></div>
    </el-card>
    <el-dialog v-model="recordsVisible" title="钱包明细" width="680px">
      <el-table :data="walletInfos" stripe>
        <el-table-column prop="detail" label="交易描述" min-width="220" />
        <el-table-column label="金额" width="140" align="right"><template #default="{ row }"><span :class="row.surplusMoney >= 0 ? 'income' : 'expense'">{{ row.surplusMoney >= 0 ? '+' : '' }}¥{{ formatMoney(row.surplusMoney) }}</span></template></el-table-column>
        <el-table-column prop="createTime" label="交易时间" min-width="170" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getWalletInfoList, getWalletList } from '@/api/wallet'
import SkeletonScreen from '@/components/SkeletonScreen.vue'
import type { WalletInfo, WalletListItem, WalletListParams } from '@/types/wallet'

const loading = ref(false)
const wallets = ref<WalletListItem[]>([])
const total = ref(0)
const page = ref(1)
const recordsVisible = ref(false)
const walletInfos = ref<WalletInfo[]>([])
const query = reactive<WalletListParams>({ current: 0, size: 10, username: '', type: null, status: null })
const formatMoney = (value: number) => Number(value || 0).toFixed(2)
const loadWallets = async () => { loading.value = true; try { const res = await getWalletList({ ...query, current: page.value - 1 }); wallets.value = res.data || []; total.value = Number(res.count || 0) } catch (error: any) { ElMessage.error(error.message || '查询钱包列表失败') } finally { loading.value = false } }
const search = () => { page.value = 1; loadWallets() }
const reset = () => { query.username = ''; query.type = null; query.status = null; search() }
const showRecords = async (wallet: WalletListItem) => { try { const res = await getWalletInfoList(wallet.id); walletInfos.value = res.data || []; recordsVisible.value = true } catch (error: any) { ElMessage.error(error.message || '查询钱包明细失败') } }
onMounted(loadWallets)
</script>

<style scoped lang="scss">
.wallet-management {
  padding: 28px 0 8px;
}

h1 {
  margin: 0 0 22px;
  color: var(--ink-900);
  font-size: 24px;
  font-weight: 750;
  letter-spacing: -0.03em;
}

.wallet-management :deep(.el-card__body) {
  padding: 22px 24px 24px;
}

.wallet-management :deep(.el-card > .el-card__body > .el-form) {
  display: flex;
  gap: 0 4px;
  align-items: flex-end;
  margin-bottom: 20px;
  padding: 16px;
  border: 1px solid #f0e6dd;
  border-radius: 12px;
  background: #fffaf6;
}

.wallet-management :deep(.el-input) {
  width: min(240px, 52vw);
}

.wallet-management :deep(.el-select) {
  width: 132px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 22px;
  padding-top: 18px;
  border-top: 1px solid var(--line);
}

.income {
  color: var(--success);
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}

.expense {
  color: var(--danger);
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}

@media (max-width: 640px) {
  .wallet-management {
    padding-top: 18px;
  }

  h1 {
    margin-bottom: 16px;
    font-size: 21px;
  }

  .wallet-management :deep(.el-card__body) {
    padding: 16px;
  }

  .wallet-management :deep(.el-card > .el-card__body > .el-form) {
    display: block;
    padding: 12px;
  }

  .wallet-management :deep(.el-form-item) {
    display: flex;
    margin: 0 0 12px;
  }

  .wallet-management :deep(.el-form-item__content) {
    flex: 1;
  }

  .wallet-management :deep(.el-input),
  .wallet-management :deep(.el-select) {
    width: 100%;
  }

  .pagination {
    justify-content: center;
  }
}
</style>
