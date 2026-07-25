<template>
  <div class="dish-browse">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索菜品..."
        clearable
        @keyup.enter="handleSearch"
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 分类筛选 -->
    <div class="category-filter">
      <el-radio-group v-model="selectedTypeId" @change="handleCategoryChange">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button
          v-for="item in dishTypeList"
          :key="item.id"
          :label="item.id"
        >
          {{ item.name }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 菜品列表 -->
    <SkeletonScreen v-if="loading" variant="grid" :count="8" />
    <el-empty v-else-if="dishList.length === 0" description="暂无菜品" />
    <div v-else class="dish-grid">
      <div v-for="dish in dishList" :key="dish.id" class="dish-card card-glow" @click="handleViewDetail(dish)">
        <div class="dish-cover">
          <el-image
            v-if="dish.coverUrl"
            v-lazy
            lazy
            :src="dish.coverUrl"
            fit="cover"
            class="cover-image"
          />
          <div v-else class="no-cover">暂无图片</div>
          <el-tag
            v-if="!dish.status"
            type="danger"
            class="status-tag"
          >
            已下架
          </el-tag>
        </div>
        <div class="dish-info">
          <h3 class="dish-name">{{ dish.name }}</h3>
          <p class="dish-detail">{{ dish.detail }}</p>
          <div class="dish-meta">
            <el-tag size="small">{{ dish.typeName || '未分类' }}</el-tag>
            <span class="dish-price">
              ¥{{ getMinPrice(dish) }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="pagination.total > 0">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        layout="prev, pager, next"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 菜品详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      :title="currentDish?.name"
      width="700px"
      top="5vh"
    >
      <div class="dish-detail" v-if="currentDish">
        <div class="detail-cover">
          <el-image
            v-if="currentDish.coverUrl"
            v-lazy
            :src="currentDish.coverUrl"
            fit="cover"
            class="detail-image"
          />
        </div>
        <div class="detail-info">
          <div class="detail-title">
            <h2>{{ currentDish.name }}</h2>
            <el-button
              v-if="!authStore.isAdmin"
              :type="collected ? 'danger' : 'primary'"
              plain
              @click="toggleFavorite"
            >{{ collected ? '取消收藏' : '收藏菜品' }}</el-button>
          </div>
          <el-tag>{{ currentDish.typeName || '未分类' }}</el-tag>
          <p class="detail-text">{{ currentDish.detail }}</p>
        </div>

        <!-- 套餐列表 -->
        <div class="package-section">
          <h3>套餐选择</h3>
          <el-table :data="currentDish.dishesPackageList" border>
            <el-table-column prop="name" label="套餐名" />
            <el-table-column prop="specs" label="规格" />
            <el-table-column prop="price" label="价格" width="120">
              <template #default="{ row }">
                <span class="price">¥{{ row.price?.toFixed(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleAddCart(row)">加入购物车</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 评价区域 -->
        <div class="evaluation-section">
          <h3>用户评价</h3>
          <div v-loading="evaluationLoading">
            <el-empty v-if="!evaluationLoading && evaluationList.length === 0" description="暂无评价" />
            <div v-for="evalItem in evaluationList" :key="evalItem.id" class="evaluation-item">
              <div class="eval-header">
                <el-avatar v-lazy :size="32" :src="evalItem.avatar">
                  {{ evalItem.username?.charAt(0) || 'U' }}
                </el-avatar>
                <span class="eval-user">{{ evalItem.username }}</span>
                <el-rate v-model="evalItem.ratingValue" disabled show-score />
                <span class="eval-time">{{ evalItem.createTime }}</span>
              </div>
              <p class="eval-content">{{ evalItem.content }}</p>
              <div v-if="evalItem.imagesList?.length" class="eval-images">
                <el-image
                  v-for="img in evalItem.imagesList"
                  :key="img.id"
                  v-lazy
                  lazy
                  :src="img.pictureUrl"
                  :preview-src-list="evalItem.imagesList.map(i => i.pictureUrl)"
                  fit="cover"
                  class="eval-image"
                />
              </div>
              <div v-if="evalItem.replyContent" class="eval-reply">
                <el-tag type="info" size="small">商家回复</el-tag>
                <span>{{ evalItem.replyContent }}</span>
              </div>
            </div>
            <el-card v-if="!authStore.isAdmin" class="submit-evaluation" shadow="never">
              <template #header>发表评价</template>
              <el-rate v-model="evaluationForm.ratingValue" show-score />
              <el-input
                v-model="evaluationForm.content"
                class="evaluation-input"
                type="textarea"
                :rows="3"
                maxlength="300"
                show-word-limit
                placeholder="分享一下这道菜的用餐感受"
              />
              <el-upload
                multiple
                :limit="6"
                list-type="picture-card"
                :http-request="uploadEvaluationImage"
                :on-remove="removeEvaluationImage"
              >
                <el-icon><Plus /></el-icon>
              </el-upload>
              <el-button type="primary" :loading="submittingEvaluation" @click="submitEvaluation">提交评价</el-button>
            </el-card>
          </div>
          <div class="eval-pagination" v-if="evalPagination.total > 10">
            <el-pagination
              v-model:current-page="evalPagination.current"
              :page-size="evalPagination.size"
              :total="evalPagination.total"
              layout="prev, pager, next"
              @current-change="handleEvalPageChange"
            />
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { getDishList, getDishTypeList as fetchDishTypeList, getDishDetail } from '@/api/dish'
import { addCartItem } from '@/api/cart'
import { getEvaluationList, saveEvaluation } from '@/api/evaluation'
import { isCollected, toggleCollection } from '@/api/collection'
import { uploadFile } from '@/api/user'
import { useAuthStore } from '@/stores/auth'
import SkeletonScreen from '@/components/SkeletonScreen.vue'
import type { Dish, DishType } from '@/types/dish'
import type { Evaluation } from '@/types/evaluation'
import type { UploadFile, UploadRequestOptions } from 'element-plus'

const route = useRoute()
const authStore = useAuthStore()

// 评价类型
// 菜品分类列表
const dishTypeList = ref<DishType[]>([])

// 搜索
const searchKeyword = ref('')
const selectedTypeId = ref<number | null>(null)

// 分页
const pagination = reactive({
  current: 1,
  size: 12,
  total: 0
})

// 菜品列表
const dishList = ref<Dish[]>([])
const loading = ref(false)

// 详情对话框
const detailVisible = ref(false)
const currentDish = ref<Dish | null>(null)

// 评价
const evaluationList = ref<Evaluation[]>([])
const evaluationLoading = ref(false)
const evalPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})
const collected = ref(false)
const submittingEvaluation = ref(false)
const evaluationImageUrls = ref<string[]>([])
const evaluationForm = reactive({
  content: '',
  ratingValue: 5
})

// 加载菜品分类
const loadDishTypes = async () => {
  try {
    const res = await fetchDishTypeList({ current: 0, size: 100 })
    if (res.code === 200) {
      dishTypeList.value = res.data || []
    }
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

// 加载菜品数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getDishList({
      current: pagination.current - 1,
      size: pagination.size,
      name: searchKeyword.value || undefined,
      typeId: selectedTypeId.value,
      status: 1 // 只显示上架菜品
    })
    if (res.code === 200) {
      dishList.value = res.data || []
      pagination.total = res.count || 0
    } else {
      ElMessage.error(res.message || '查询失败')
    }
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 分类切换
const handleCategoryChange = () => {
  pagination.current = 1
  loadData()
}

// 页码变化
const handleCurrentChange = (val: number) => {
  pagination.current = val
  loadData()
}

// 获取最低价格
const getMinPrice = (dish: Dish) => {
  if (!dish.dishesPackageList?.length) return '0.00'
  const min = Math.min(...dish.dishesPackageList.map(p => p.price))
  return min.toFixed(2)
}

const handleAddCart = async (dishesPackage: { id?: number }) => {
  if (!dishesPackage.id) {
    ElMessage.error('菜品套餐信息异常')
    return
  }
  try {
    await addCartItem({ dishesPackageId: dishesPackage.id, plusNumber: 1 })
    ElMessage.success('已加入购物车')
  } catch { /* 统一响应拦截器已提示 */ }
}

// 查看详情
const handleViewDetail = async (dish: Dish) => {
  currentDish.value = dish
  detailVisible.value = true
  evalPagination.current = 1
  evaluationForm.content = ''
  evaluationForm.ratingValue = 5
  evaluationImageUrls.value = []
  await Promise.all([loadEvaluations(dish.id), loadCollectionStatus(dish.id)])
}

// 加载评价
const loadEvaluations = async (dishesId: number) => {
  // 评价模块尚未开发。详情页仅展示已完成的菜品和套餐信息，避免请求不存在的接口。
  evaluationLoading.value = true
  try {
    const res = await getEvaluationList({
      current: evalPagination.current - 1,
      size: evalPagination.size,
      dishesId
    })
    if (res.code === 200) {
      evaluationList.value = res.data || []
      evalPagination.total = res.count || 0
    }
  } catch (error) {
    console.error('加载评价失败', error)
  } finally {
    evaluationLoading.value = false
  }
}

// 评价分页变化
const loadCollectionStatus = async (dishesId: number) => {
  if (authStore.isAdmin) {
    collected.value = false
    return
  }
  try {
    const res = await isCollected(dishesId)
    collected.value = Boolean(res.data)
  } catch {
    collected.value = false
  }
}

const toggleFavorite = async () => {
  if (!currentDish.value) return
  try {
    await toggleCollection(currentDish.value.id)
    collected.value = !collected.value
    ElMessage.success(collected.value ? '已收藏' : '已取消收藏')
  } catch { /* 统一响应拦截器已提示 */ }
}

const uploadEvaluationImage = async (options: UploadRequestOptions) => {
  try {
    const res = await uploadFile(options.file)
    const pictureUrl = res.data
    if (!pictureUrl) throw new Error('上传图片失败')
    evaluationImageUrls.value.push(pictureUrl)
    options.onSuccess?.(res)
  } catch (error: any) {
    options.onError?.(error)
  }
}

const removeEvaluationImage = (file: UploadFile) => {
  const pictureUrl = (file.response as { data?: string } | undefined)?.data || file.url
  if (pictureUrl) {
    evaluationImageUrls.value = evaluationImageUrls.value.filter(url => url !== pictureUrl)
  }
}

const submitEvaluation = async () => {
  if (!currentDish.value) return
  if (!evaluationForm.content.trim()) {
    ElMessage.warning('请填写评价内容')
    return
  }
  submittingEvaluation.value = true
  try {
    await saveEvaluation({
      dishesEvaluations: {
        dishesId: currentDish.value.id,
        content: evaluationForm.content.trim(),
        ratingValue: evaluationForm.ratingValue
      },
      imagesList: evaluationImageUrls.value.map((pictureUrl, number) => ({ pictureUrl, number }))
    })
    ElMessage.success('评价提交成功')
    evaluationForm.content = ''
    evaluationImageUrls.value = []
    await loadEvaluations(currentDish.value.id)
  } finally {
    submittingEvaluation.value = false
  }
}

const handleEvalPageChange = (val: number) => {
  evalPagination.current = val
  if (currentDish.value) {
    loadEvaluations(currentDish.value.id)
  }
}

// 监听路由参数变化
watch(() => route.query, (query) => {
  if (query.typeId) {
    selectedTypeId.value = Number(query.typeId)
  }
  if (query.dishId) {
    // 打开菜品详情
    const dishId = Number(query.dishId)
    getDishDetail(dishId).then(res => {
      if (res.code === 200 && res.data) {
        handleViewDetail(res.data)
      }
    })
  }
}, { immediate: true })

onMounted(() => {
  loadDishTypes()
  loadData()
})
</script>

<style scoped lang="scss">
.dish-browse {
  width: min(1360px, 100%);
  padding: 32px clamp(16px, 3vw, 36px) 48px;
  margin: 0 auto;
}

.search-bar {
  display: flex;
  align-items: center;
  min-height: 82px;
  margin-bottom: 14px;
  padding: 18px 20px;
  border: 1px solid #eee1d7;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 5px 15px rgba(65, 42, 28, 0.05);

  .search-input {
    max-width: 460px;

    :deep(.el-input__wrapper) {
      min-height: 46px;
      padding: 0 20px;
      border-radius: 999px;
    }
  }
}

.category-filter {
  margin-bottom: 28px;
  padding: 6px 0;
  overflow-x: auto;

  :deep(.el-radio-group) {
    display: flex;
    flex-wrap: nowrap;
    gap: 8px;
  }

  :deep(.el-radio-button__inner) {
    border: 1px solid #eadfd7 !important;
    border-radius: 999px !important;
    box-shadow: none !important;
    color: #6b625c;
    font-weight: 600;
  }

  :deep(.el-radio-button:first-child .el-radio-button__inner),
  :deep(.el-radio-button:last-child .el-radio-button__inner) {
    border-radius: 999px !important;
  }

  :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
    border-color: #c9502d !important;
    background: #c9502d;
    box-shadow: 0 6px 13px rgba(201, 80, 45, 0.18) !important;
    color: #fff;
  }
}

.dish-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(245px, 1fr));
  gap: 20px;
  min-height: 200px;
}

.dish-card {
  border: 1px solid #eee3da;
  border-radius: 18px;
  background: #fff;
  overflow: hidden;
  box-shadow: 0 5px 16px rgba(65, 42, 28, 0.06);
  cursor: pointer;
  transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;

  &:hover {
    border-color: #edc1ad;
    transform: translateY(-6px);
    box-shadow: 0 18px 32px rgba(92, 47, 30, 0.14);

    .cover-image {
      transform: scale(1.06);
    }
  }
}

.dish-cover {
  position: relative;
  height: 190px;
  overflow: hidden;
  background: #f8e9df;

  .cover-image {
    width: 100%;
    height: 100%;
    transition: transform 0.45s ease;
  }

  .no-cover {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background:
      radial-gradient(circle at 72% 22%, rgba(226, 106, 62, 0.22) 0 16px, transparent 17px),
      linear-gradient(135deg, #fff1e8, #f5ddd0);
    color: #9a7160;
    font-size: 13px;
  }

  .status-tag {
    position: absolute;
    top: 12px;
    right: 12px;
    box-shadow: 0 4px 10px rgba(67, 37, 26, 0.12);
  }
}

.dish-info {
  padding: 17px;

  .dish-name {
    margin: 0 0 8px;
    font-size: 16px;
    color: #30241f;
    font-weight: 700;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .dish-detail {
    font-size: 13px;
    color: #887d75;
    margin: 0 0 10px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .dish-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .dish-price {
    font-size: 18px;
    color: #c9502d;
    font-weight: 750;
    font-variant-numeric: tabular-nums;
  }
}

.pagination {
  margin-top: 34px;
  display: flex;
  justify-content: center;
}

.dish-detail {
  .detail-cover {
    margin-bottom: 22px;

    .detail-image {
      width: 100%;
      height: 320px;
      border: 1px solid #eee1d8;
      border-radius: 14px;

      :deep(.el-image__inner) {
        object-position: center center;
      }
    }
  }

  .detail-info {
    margin-bottom: 24px;

    .detail-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 12px;
    }

    h2 {
      margin: 0;
      color: #30241f;
      font-size: 24px;
    }

    .detail-text {
      margin: 10px 0 0;
      color: #756a62;
      line-height: 1.75;
    }
  }

  .package-section {
    margin-bottom: 26px;
    padding: 18px;
    border: 1px solid #f0e5dc;
    border-radius: 14px;
    background: #fffaf7;

    h3 {
      margin: 0 0 10px;
      color: #42352f;
      font-size: 17px;
    }

    .price {
      color: #c9502d;
      font-weight: 750;
    }
  }

  .evaluation-section {
    h3 {
      margin: 0 0 15px;
      color: #42352f;
      font-size: 17px;
    }

    .evaluation-item {
      margin-bottom: 12px;
      padding: 16px;
      border: 1px solid #f1e7df;
      border-radius: 12px;
      background: #fffdfb;

      .eval-header {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 10px;

        .eval-user {
          font-weight: bold;
        }

        .eval-time {
          color: #958a82;
          font-size: 12px;
          margin-left: auto;
        }
      }

      .eval-content {
        margin: 0 0 10px;
        color: #514741;
        line-height: 1.7;
      }

      .eval-images {
        display: flex;
        gap: 10px;
        flex-wrap: wrap;

        .eval-image {
          width: 80px;
          height: 80px;
          border-radius: 8px;
        }
      }

      .eval-reply {
        margin-top: 10px;
        padding: 11px;
        border-radius: 8px;
        background: #fff4ed;
        display: flex;
        align-items: flex-start;
        gap: 8px;

        span {
          color: #6e625a;
        }
      }
    }

    .eval-pagination {
      margin-top: 15px;
      display: flex;
      justify-content: center;
    }

    .submit-evaluation {
      margin-top: 18px;

      .evaluation-input {
        margin: 14px 0;
      }
    }
  }
}

@media (max-width: 768px) {
  .dish-browse {
    padding: 22px 14px 36px;
  }

  .search-bar {
    min-height: auto;
    padding: 14px;
  }

  .search-bar .search-input {
    max-width: none;
  }

  .dish-grid {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 14px;
  }

  .dish-cover {
    height: 155px;
  }

  .dish-info {
    padding: 14px;
  }

  .dish-detail .package-section {
    padding: 12px;
  }

  .dish-detail .detail-image {
    height: 220px;
  }
}

@media (max-width: 420px) {
  .dish-grid {
    grid-template-columns: 1fr 1fr;
  }

  .dish-cover {
    height: 130px;
  }
}
</style>
