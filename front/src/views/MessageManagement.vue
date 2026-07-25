<template>
  <div class="page">
    <div class="admin-page-header">
      <div>
        <h2 class="header-title">系统消息</h2>
        <p class="header-desc">向用户发送通知并查看送达状态</p>
      </div>
    </div>
    <el-form inline>
      <el-form-item label="接收用户">
        <el-select v-model="form.userId" filterable>
          <el-option v-for="u in users" :key="u.id" :label="u.username" :value="u.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="form.content" placeholder="通知内容" />
      </el-form-item>
      <el-button type="primary" @click="send">发送</el-button>
    </el-form>
    <SkeletonScreen v-if="loading" variant="table" :count="5" />
    <el-table v-else :data="items">
      <el-table-column prop="username" label="用户" />
      <el-table-column prop="content" label="内容" />
      <el-table-column prop="readStatus" label="状态">
        <template #default="{row}">{{row.readStatus?'已读':'未读'}}</template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" />
    </el-table>
  </div>
</template>
<script setup lang="ts">import{onMounted,reactive,ref}from'vue';import{ElMessage}from'element-plus';import{listMessages,sendMessages,type MessageItem}from'@/api/message';import{getUserList}from'@/api/user';import SkeletonScreen from'@/components/SkeletonScreen.vue';const items=ref<MessageItem[]>([]);const users=ref<any[]>([]);const loading=ref(true);const form=reactive({userId:undefined as number|undefined,content:''});const load=async()=>{loading.value=true;try{items.value=(await listMessages({current:0,size:100})).data||[];users.value=(await getUserList({current:0,size:100,role:1})).data||[]}finally{loading.value=false}};const send=async()=>{if(!form.userId||!form.content.trim())return ElMessage.warning('请选择用户并填写内容');await sendMessages([{userId:form.userId,content:form.content.trim()}]);form.content='';ElMessage.success('发送成功');load()};onMounted(load)</script>
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

.page > :deep(.el-form) {
  display: flex;
  gap: 0 4px;
  align-items: flex-end;
  margin: 0 0 20px;
  padding: 16px;
  border: 1px solid #f0e6dd;
  border-radius: 12px;
  background: #fffaf6;
}

.page > :deep(.el-form) .el-select {
  width: min(220px, 42vw);
}

.page > :deep(.el-form) .el-input {
  width: min(340px, 54vw);
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

  .page > :deep(.el-form) {
    display: block;
    padding: 12px;
  }

  .page > :deep(.el-form) .el-form-item {
    display: flex;
    margin: 0 0 12px;
  }

  .page > :deep(.el-form) .el-form-item__content {
    flex: 1;
  }

  .page > :deep(.el-form) .el-input,
  .page > :deep(.el-form) .el-select {
    width: 100%;
  }
}
</style>
