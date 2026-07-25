<template>
  <div class="dashboard">
    <div class="admin-page-header">
      <div>
        <h2 class="header-title">经营数据统计</h2>
        <p class="header-desc">近七日销售趋势、菜品品类分布与商家账单概览</p>
      </div>
    </div>

    <SkeletonScreen v-if="loading" variant="grid" :count="3" />
    <SkeletonScreen v-if="loading" variant="table" :count="5" />

    <template v-else>
    <el-row :gutter="16" class="summary">
      <el-col :span="8">
        <el-card class="stat-card card-glow">
          <div class="stat-icon"><el-icon><TrendCharts /></el-icon></div>
          <div class="stat-value">¥ {{ salesDisplay }}</div>
          <div class="stat-label">销售额汇总（近七日）</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card stat-card--gold card-glow">
          <div class="stat-icon"><el-icon><Dish /></el-icon></div>
          <div class="stat-value">{{ dishesDisplay }}</div>
          <div class="stat-label">菜品总数</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card stat-card--jade card-glow">
          <div class="stat-icon"><el-icon><Wallet /></el-icon></div>
          <div class="stat-value">¥ {{ surplusDisplay }}</div>
          <div class="stat-label">商家钱包余额</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" class="charts">
      <el-col :span="12">
        <el-card>
          <template #header>订单销售额趋势</template>
          <svg viewBox="0 0 600 250" class="line-chart">
            <defs>
              <linearGradient id="lineFill" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stop-color="#df6237" stop-opacity="0.26" />
                <stop offset="100%" stop-color="#df6237" stop-opacity="0" />
              </linearGradient>
            </defs>
            <polygon v-if="areaPoints" :points="areaPoints" fill="url(#lineFill)" />
            <polyline :points="linePoints" fill="none" stroke="#df6237" stroke-width="4" stroke-linecap="round" stroke-linejoin="round" />
            <circle
              v-for="(item, index) in sales"
              :key="'dot-' + item.date"
              :cx="40 + index * 82"
              :cy="210 - Number(item.value) / maxSales * 160"
              r="4.5"
              fill="#c9502d"
              stroke="#fff"
              stroke-width="2"
            />
            <line x1="30" y1="220" x2="580" y2="220" stroke="#eadfd6" />
            <text v-for="(item,index) in sales" :key="item.date" :x="38 + index * 82" y="242" class="axis">{{ item.date.slice(5) }}</text>
          </svg>
        </el-card>
      </el-col>
      <el-col :span="12"><el-card><template #header>各品类菜品数量</template><div class="pie-wrap"><div class="pie" :style="{ background: pieGradient }"></div><div><p v-for="(item, index) in types" :key="item.name"><i :style="{background: color(index)}"></i>{{ item.name }}：{{ item.value }}</p></div></div></el-card></el-col>
    </el-row>
    <el-card><template #header>商家账单明细</template><el-table :data="walletInfos"><el-table-column prop="detail" label="交易说明"/><el-table-column prop="surplusMoney" label="金额"><template #default="{row}">¥ {{ Number(row.surplusMoney).toFixed(2) }}</template></el-table-column><el-table-column prop="createTime" label="交易时间"/></el-table></el-card>
    </template>
  </div>
</template>
<script setup lang="ts">
import { computed,onMounted,ref } from 'vue'
import { TrendCharts, Dish, Wallet } from '@element-plus/icons-vue'
import { getDishTypeCount,getMerchantWallet,getOrderSales } from '@/api/dashboard'
import { getWalletInfoList } from '@/api/wallet'
import SkeletonScreen from '@/components/SkeletonScreen.vue'
import { useCountUp } from '@/composables/useCountUp'
const loading=ref(false),wallet=ref<any>(),sales=ref<any[]>([]),types=ref<any[]>([]),walletInfos=ref<any[]>([])
const totalSales=computed(()=>sales.value.reduce((sum,x)=>sum+Number(x.value||0),0));const totalDishes=computed(()=>types.value.reduce((sum,x)=>sum+Number(x.value||0),0))
const maxSales=computed(()=>Math.max(...sales.value.map(x=>Number(x.value)),1))
const linePoints=computed(()=>sales.value.map((x,i)=>`${40+i*82},${210-Number(x.value)/maxSales.value*160}`).join(' '))
const areaPoints=computed(()=>{if(!sales.value.length)return '';const lastX=40+(sales.value.length-1)*82;return `${linePoints.value} ${lastX},220 40,220`})
const salesDisplay=useCountUp(totalSales,{decimals:2});const dishesDisplay=useCountUp(totalDishes)
const surplusDisplay=useCountUp(computed(()=>Number(wallet.value?.surplus||0)),{decimals:2})
const colors=['#c9502d','#bf7a16','#23836b','#8c5a3d','#e2a63d','#5a7ba6','#df6237','#8a5aa0'];const color=(index:number)=>index<colors.length?colors[index]:`hsl(${(index*47)%360},52%,55%)`
const pieGradient=computed(()=>{const all=totalDishes.value||1;let at=0;return `conic-gradient(${types.value.map((x:any,i:number)=>{const next=at+Number(x.value)/all*100;const s=`${color(i)} ${at}% ${next}%`;at=next;return s}).join(',')})`})
onMounted(async()=>{loading.value=true;try{const [w,s,t]=await Promise.all([getMerchantWallet(),getOrderSales(),getDishTypeCount()]);wallet.value=w.data;sales.value=s.data||[];types.value=t.data||[];if(wallet.value?.id)walletInfos.value=(await getWalletInfoList(wallet.value.id)).data||[]}finally{loading.value=false}})
</script>
<style scoped>
.dashboard {
  padding: 28px 0 8px;
}

.summary,
.charts {
  margin-bottom: 20px;
}

.skeleton-grid {
  margin-bottom: 20px;
}

/* stat-card 基础结构由全局 style.css 提供，这里适配 el-card 外壳 */
.stat-card {
  min-height: 150px;
  border: 0;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 0;
}

.stat-card--gold .stat-icon {
  background: var(--gradient-gold);
}

.stat-card--jade .stat-icon {
  background: var(--gradient-jade);
}

.charts :deep(.el-card) {
  height: 100%;
}

.line-chart {
  display: block;
  width: 100%;
  height: 250px;
  padding: 8px 4px 0;
  border-radius: 10px;
  background: linear-gradient(180deg, rgba(255, 245, 238, 0.7), rgba(255, 255, 255, 0));
}

.axis {
  fill: var(--ink-500);
  font-size: 11px;
}

.pie-wrap {
  display: flex;
  gap: clamp(24px, 4vw, 42px);
  align-items: center;
  min-height: 250px;
  padding: 8px 4px;
}

.pie {
  flex: 0 0 auto;
  width: clamp(148px, 17vw, 190px);
  height: clamp(148px, 17vw, 190px);
  border: 10px solid #fff;
  border-radius: 50%;
  box-shadow: 0 8px 22px rgba(65, 42, 28, 0.1);
}

.pie-wrap p {
  display: flex;
  gap: 8px;
  align-items: center;
  margin: 10px 0;
  color: var(--ink-700);
  font-size: 13px;
  font-weight: 600;
}

.pie-wrap i {
  display: inline-block;
  width: 9px;
  height: 9px;
  border-radius: 50%;
  box-shadow: 0 0 0 3px rgba(223, 98, 55, 0.08);
}

@media (max-width: 900px) {
  .summary :deep(.el-col),
  .charts :deep(.el-col) {
    flex: 0 0 100%;
    max-width: 100%;
    margin-bottom: 16px;
  }

  .summary :deep(.el-col:last-child),
  .charts :deep(.el-col:last-child) {
    margin-bottom: 0;
  }
}

@media (max-width: 560px) {
  .dashboard {
    padding-top: 18px;
  }

  .stat-card {
    min-height: 132px;
  }

  .pie-wrap {
    flex-direction: column;
    align-items: flex-start;
    min-height: auto;
  }
}
</style>
