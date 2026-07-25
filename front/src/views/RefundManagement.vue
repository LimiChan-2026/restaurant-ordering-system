<template>
  <div class="page">
    <div class="admin-page-header">
      <div>
        <h2 class="header-title">退款审核</h2>
        <p class="header-desc">审核用户提交的退款申请</p>
      </div>
    </div>
    <SkeletonScreen v-if="loading" variant="table" :count="5" />
    <el-table v-else :data="items">
      <el-table-column prop="ordersId" label="订单ID" width="90" />
      <el-table-column prop="refundCause" label="退款原因" />
      <el-table-column label="状态" width="100">
        <template #default="{row}"><el-tag :type="tag(row.status)">{{label(row.status)}}</el-tag></template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" />
      <el-table-column label="操作" width="180">
        <template #default="{row}">
          <el-button v-if="row.status===1" type="success" link @click="audit(row,2)">通过</el-button>
          <el-button v-if="row.status===1" type="danger" link @click="reject(row)">拒绝</el-button>
          <span v-else>{{row.rejectRefundCause||'-'}}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup lang="ts">import{onMounted,ref}from'vue';import{ElMessage,ElMessageBox}from'element-plus';import{auditRefund,getRefundList,type RefundRecord}from'@/api/refund';import{refreshHeaderIndicators}from'@/utils/headerIndicator';import SkeletonScreen from'@/components/SkeletonScreen.vue';const loading=ref(false),items=ref<RefundRecord[]>([]);const label=(s:number)=>s===1?'待审核':s===2?'已退款':'已拒绝';const tag=(s:number)=>s===1?'warning':s===2?'success':'danger';const load=async()=>{loading.value=true;try{items.value=(await getRefundList()).data||[]}finally{loading.value=false}};const audit=async(r:RefundRecord,s:2|3,c='')=>{await auditRefund(r.id,s,c);ElMessage.success('审核完成');await load();refreshHeaderIndicators()};const reject=async(r:RefundRecord)=>{const{value}=await ElMessageBox.prompt('请填写拒绝原因','拒绝退款',{inputPattern:/\S+/,inputErrorMessage:'拒绝原因不能为空'});audit(r,3,value)};onMounted(load)</script>
<style scoped>
.page {
  padding-top: 28px;
}

h2 {
  margin-bottom: 22px;
  color: var(--ink-900);
  font-size: 24px;
  font-weight: 750;
  letter-spacing: -0.03em;
}

.page :deep(.el-table) {
  border-radius: 16px;
  box-shadow: var(--shadow-sm);
}

@media (max-width: 640px) {
  .page {
    padding-top: 18px;
  }

  h2 {
    margin-bottom: 16px;
    font-size: 21px;
  }
}
</style>
