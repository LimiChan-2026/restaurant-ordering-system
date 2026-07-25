<template>
  <div class="skeleton-screen" :class="`skeleton-${variant}`" aria-hidden="true">
    <template v-if="variant === 'grid'">
      <div v-for="i in count" :key="i" class="skeleton-card">
        <div class="skeleton-item skeleton-cover"></div>
        <div class="skeleton-body">
          <div class="skeleton-item skeleton-line" style="width: 72%"></div>
          <div class="skeleton-item skeleton-line" style="width: 48%"></div>
          <div class="skeleton-item skeleton-line skeleton-line--short" style="width: 30%"></div>
        </div>
      </div>
    </template>

    <template v-else-if="variant === 'table'">
      <div class="skeleton-table-head">
        <div class="skeleton-item skeleton-cell" style="width: 18%"></div>
        <div class="skeleton-item skeleton-cell" style="width: 26%"></div>
        <div class="skeleton-item skeleton-cell" style="width: 22%"></div>
        <div class="skeleton-item skeleton-cell" style="width: 14%"></div>
      </div>
      <div v-for="i in count" :key="i" class="skeleton-table-row">
        <div class="skeleton-item skeleton-cell" style="width: 18%"></div>
        <div class="skeleton-item skeleton-cell" style="width: 26%"></div>
        <div class="skeleton-item skeleton-cell" style="width: 22%"></div>
        <div class="skeleton-item skeleton-cell" style="width: 14%"></div>
      </div>
    </template>

    <template v-else>
      <div v-for="i in count" :key="i" class="skeleton-list-item">
        <div class="skeleton-item skeleton-thumb"></div>
        <div class="skeleton-list-lines">
          <div class="skeleton-item skeleton-line" style="width: 58%"></div>
          <div class="skeleton-item skeleton-line" style="width: 84%"></div>
          <div class="skeleton-item skeleton-line" style="width: 38%"></div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    /** grid=菜品卡片网格，table=管理表格，list=订单/消息列表 */
    variant?: 'grid' | 'table' | 'list'
    count?: number
  }>(),
  {
    variant: 'list',
    count: 4
  }
)
</script>

<style scoped lang="scss">
.skeleton-screen {
  width: 100%;
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(245px, 1fr));
  gap: 20px;
}

.skeleton-card {
  overflow: hidden;
  border: 1px solid var(--line);
  border-radius: 18px;
  background: var(--surface);
  box-shadow: var(--shadow-xs);
}

.skeleton-cover {
  height: 168px;
  border-radius: 0;
}

.skeleton-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 16px;
}

.skeleton-line {
  height: 14px;
}

.skeleton-line--short {
  height: 18px;
}

.skeleton-table-head,
.skeleton-table-row {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 14px 18px;
}

.skeleton-table-head {
  border-radius: 12px 12px 0 0;
  background: #fbf6f1;
}

.skeleton-table-row {
  border-bottom: 1px solid var(--line);
  background: var(--surface);

  &:last-child {
    border-bottom: none;
    border-radius: 0 0 12px 12px;
  }
}

.skeleton-cell {
  height: 15px;
}

.skeleton-list-item {
  display: flex;
  gap: 16px;
  padding: 18px;
  margin-bottom: 14px;
  border: 1px solid var(--line);
  border-radius: var(--radius-md);
  background: var(--surface);
  box-shadow: var(--shadow-xs);
}

.skeleton-thumb {
  flex: 0 0 auto;
  width: 72px;
  height: 72px;
  border-radius: 12px;
}

.skeleton-list-lines {
  display: flex;
  flex: 1;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
}
</style>
