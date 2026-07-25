<template>
  <div class="evaluation-management">
    <div class="admin-page-header">
      <div>
        <h1 class="header-title">评价管理</h1>
        <p class="header-desc">查看用户评价并进行回复</p>
      </div>
    </div>
    <el-card shadow="never">
      <SkeletonScreen v-if="loading" variant="table" :count="5" />
      <el-table v-else :data="evaluations" stripe>
        <el-table-column prop="dishesName" label="菜品" min-width="130" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column label="评分" width="150">
          <template #default="{ row }"><el-rate :model-value="row.ratingValue" disabled /></template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="220" show-overflow-tooltip />
        <el-table-column label="回复" min-width="200">
          <template #default="{ row }">{{ row.replyContent || '未回复' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }"><el-button type="primary" link @click="openReply(row)">回复</el-button></template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="prev, pager, next" @current-change="load" />
      </div>
    </el-card>
    <el-dialog v-model="dialogVisible" title="回复评价" width="460px">
      <el-input v-model="replyContent" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="请输入回复内容" />
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" :loading="replying" @click="submitReply">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'; import { ElMessage } from 'element-plus'; import { getEvaluationList, replyEvaluation } from '@/api/evaluation'; import SkeletonScreen from '@/components/SkeletonScreen.vue'; import type { Evaluation } from '@/types/evaluation'
const loading=ref(false), evaluations=ref<Evaluation[]>([]), total=ref(0), page=ref(1), size=10, dialogVisible=ref(false), replying=ref(false), selected=ref<Evaluation|null>(null), replyContent=ref('')
const load=async()=>{loading.value=true;try{const res=await getEvaluationList({current:page.value-1,size,dishesId:null,userId:null});evaluations.value=res.data||[];total.value=Number(res.count||0)}catch(error:any){ElMessage.error(error.message||'查询评价失败')}finally{loading.value=false}}
const openReply=(item:Evaluation)=>{selected.value=item;replyContent.value=item.replyContent||'';dialogVisible.value=true}
const submitReply=async()=>{if(!selected.value||!replyContent.value.trim()){ElMessage.warning('请输入回复内容');return}replying.value=true;try{await replyEvaluation(selected.value.id,replyContent.value);ElMessage.success('回复成功');dialogVisible.value=false;await load()}catch(error:any){ElMessage.error(error.message||'回复失败')}finally{replying.value=false}}
onMounted(load)
</script>
<style scoped lang="scss">
.evaluation-management {
  padding: 28px 0 8px;
}

h1 {
  margin: 0 0 22px;
  color: var(--ink-900);
  font-size: 24px;
  font-weight: 750;
  letter-spacing: -0.03em;
}

.evaluation-management :deep(.el-card__body) {
  padding: 22px 24px 24px;
}

.evaluation-management :deep(.el-rate) {
  --el-rate-fill-color: #e4a54d;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 22px;
  padding-top: 18px;
  border-top: 1px solid var(--line);
}

@media (max-width: 640px) {
  .evaluation-management {
    padding-top: 18px;
  }

  h1 {
    margin-bottom: 16px;
    font-size: 21px;
  }

  .evaluation-management :deep(.el-card__body) {
    padding: 16px;
  }

  .pagination {
    justify-content: center;
  }
}
</style>
