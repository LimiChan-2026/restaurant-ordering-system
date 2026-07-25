<template>
  <div class="page">
    <div class="page-header">
      <h2>我的消息</h2>
      <el-button type="primary" @click="readAll">全部标为已读</el-button>
    </div>
    <SkeletonScreen v-if="loading" variant="list" :count="4" />
    <el-empty v-else-if="!items.length" description="暂无消息" />
    <template v-else>
      <el-card v-for="item in items" :key="item.id" class="message" :class="{ unread: !item.readStatus }">
        <div>{{ item.content }}</div>
        <small>{{ item.createTime }}</small>
      </el-card>
    </template>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listUserMessages, readAllMessages, type MessageItem } from '@/api/message'
import { refreshHeaderIndicators } from '@/utils/headerIndicator'
import SkeletonScreen from '@/components/SkeletonScreen.vue'

const items = ref<MessageItem[]>([])
const loading = ref(true)

const load = async () => {
  loading.value = true
  try {
    const r = await listUserMessages({ current: 0, size: 100 })
    items.value = r.data || []
  } finally {
    loading.value = false
  }
}

const readAll = async () => {
  await readAllMessages()
  ElMessage.success('消息已全部标记为已读')
  await load()
  refreshHeaderIndicators()
}

onMounted(load)
</script>
<style scoped>
.page {
  max-width: 960px;
  padding-top: 32px;
}

.page-header {
  display: flex;
  gap: 16px;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 22px;
}

h2 {
  margin: 0;
  color: var(--ink-900);
  font-size: 25px;
  font-weight: 750;
  letter-spacing: -0.03em;
}

.message {
  position: relative;
  overflow: hidden;
  margin: 0 0 12px;
  border-color: #eee3da;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.message:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 26px rgba(65, 42, 28, 0.1);
}

.message :deep(.el-card__body) {
  padding: 18px 20px;
}

.message :deep(.el-card__body) > div {
  color: var(--ink-700);
  font-size: 15px;
  line-height: 1.7;
}

.unread {
  border-color: #f0d6c7;
  background: linear-gradient(90deg, #fff6f0, #fff 42%);
}

.unread::before {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  width: 4px;
  background: var(--brand-500);
  content: '';
}

small {
  display: block;
  margin-top: 9px;
  color: var(--ink-500);
  font-size: 12px;
}

@media (max-width: 640px) {
  .page {
    padding-top: 22px;
  }

  .page-header {
    align-items: flex-start;
    flex-direction: column;
  }

  h2 {
    font-size: 21px;
  }

  .page-header :deep(.el-button) {
    width: 100%;
  }

  .message :deep(.el-card__body) {
    padding: 16px 18px;
  }
}
</style>
