<template>
  <div class="collection-page">
    <h1>我的收藏</h1>
    <SkeletonScreen v-if="loading" variant="list" :count="3" />
    <el-empty v-else-if="items.length === 0" description="暂无收藏菜品" />
    <template v-else>
      <el-card v-for="item in items" :key="item.id" class="collection-card">
        <el-image v-lazy lazy :src="item.dishesCover" fit="cover" class="cover">
          <template #error><div class="placeholder">暂无图片</div></template>
        </el-image>
        <div class="info">
          <strong>{{ item.dishesName }}</strong>
          <span>{{ item.dishesDetail || '暂无介绍' }}</span>
          <el-tag :type="item.dishesStatus ? 'success' : 'info'">{{ item.dishesStatus ? '上架中' : '已下架' }}</el-tag>
        </div>
        <el-button type="danger" link @click="remove(item)">取消收藏</el-button>
      </el-card>
    </template>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCollectionList, toggleCollection } from '@/api/collection'
import type { CollectionItem } from '@/types/evaluation'
import SkeletonScreen from '@/components/SkeletonScreen.vue'
const loading = ref(false); const items = ref<CollectionItem[]>([])
const load = async () => { loading.value = true; try { const res = await getCollectionList(); items.value = res.data || [] } catch (error: any) { ElMessage.error(error.message || '查询收藏失败') } finally { loading.value = false } }
const remove = async (item: CollectionItem) => { try { await toggleCollection(item.dishesId); ElMessage.success('已取消收藏'); await load() } catch (error: any) { ElMessage.error(error.message || '操作失败') } }
onMounted(load)
</script>
<style scoped lang="scss">.collection-page{max-width:960px;margin:0 auto;padding:34px 20px 44px}h1{margin:0 0 20px;color:#30241f;font-size:28px;font-weight:750}.collection-card{display:flex;align-items:center;gap:18px;margin-bottom:14px;border-radius:16px;transition:transform .2s ease,box-shadow .2s ease;&:hover{transform:translateY(-2px);box-shadow:0 12px 26px rgba(65,42,28,.09)}}.cover{width:90px;height:90px;flex:none;border:1px solid #f0e3db;border-radius:12px}.placeholder{display:grid;width:100%;height:100%;place-items:center;background:#fff0e8;color:#9a7869;font-size:12px}.info{display:flex;flex:1;flex-direction:column;gap:8px;color:#796f68}.info strong{color:#352923;font-size:16px}@media(max-width:700px){.collection-page{padding:22px 14px 34px}h1{font-size:24px}.collection-card{gap:14px}.cover{width:72px;height:72px}}</style>
