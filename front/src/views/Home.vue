<template>
  <div class="home-page">
    <!-- 轮播图区域 -->
    <section class="banner-section">
      <el-carousel height="400px">
        <el-carousel-item v-for="banner in banners" :key="banner.id">
          <div class="banner-item" :style="{ background: banner.bg }">
            <div class="banner-content">
              <h2>{{ banner.title }}</h2>
              <p>{{ banner.desc }}</p>
              <el-button type="primary" size="large" @click="router.push('/dishes')">
                立即点餐
              </el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 分类导航 -->
    <section class="category-section">
      <div class="section-container">
        <h2 class="section-title">菜品分类</h2>
        <SkeletonScreen v-if="categoriesLoading" variant="grid" :count="4" />
        <div v-else class="category-grid">
          <div
            v-for="category in categories"
            :key="category.id"
            class="category-item"
            @click="handleCategoryClick(category)"
          >
            <el-image
              v-if="category.iconUrl"
              v-lazy
              lazy
              :src="category.iconUrl"
              fit="cover"
              class="category-icon"
            />
            <div v-else class="category-icon-placeholder">
              <el-icon size="32"><Menu /></el-icon>
            </div>
            <span class="category-name">{{ category.name }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门推荐 -->
    <section class="hot-section">
      <div class="section-container">
        <h2 class="section-title">热门推荐</h2>
        <SkeletonScreen v-if="dishesLoading" variant="grid" :count="8" />
        <div v-else class="dish-grid">
          <div
            v-for="dish in hotDishes"
            :key="dish.id"
            class="dish-card card-glow"
            @click="handleDishClick(dish)"
          >
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
            </div>
            <div class="dish-info">
              <h3 class="dish-name">{{ dish.name }}</h3>
              <p class="dish-detail">{{ dish.detail }}</p>
              <div class="dish-bottom">
                <span class="dish-price">
                  ¥{{ getMinPrice(dish) }}
                  <span class="price-suffix">起</span>
                </span>
                <el-button type="primary" size="small" @click.stop="handleDishClick(dish)">
                  查看详情
                </el-button>
              </div>
            </div>
          </div>
        </div>
        <div class="more-btn">
          <el-button @click="router.push('/dishes')">查看更多菜品</el-button>
        </div>
      </div>
    </section>

    <!-- 特色服务 -->
    <section class="service-section">
      <div class="section-container">
        <h2 class="section-title">我们的服务</h2>
        <div class="service-grid">
          <div class="service-item">
            <div class="service-icon service-icon--brand">
              <el-icon size="24"><Timer /></el-icon>
            </div>
            <h3>快速出餐</h3>
            <p>高效厨房，最短时间出餐</p>
          </div>
          <div class="service-item">
            <div class="service-icon service-icon--jade">
              <el-icon size="24"><CircleCheck /></el-icon>
            </div>
            <h3>品质保证</h3>
            <p>新鲜食材，严格品控</p>
          </div>
          <div class="service-item">
            <div class="service-icon service-icon--gold">
              <el-icon size="24"><Star /></el-icon>
            </div>
            <h3>优质服务</h3>
            <p>贴心服务，顾客至上</p>
          </div>
          <div class="service-item">
            <div class="service-icon service-icon--deep">
              <el-icon size="24"><Wallet /></el-icon>
            </div>
            <h3>优惠活动</h3>
            <p>定期优惠，会员专享</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Menu, Timer, CircleCheck, Star, Wallet } from '@element-plus/icons-vue'
import { getDishList, getDishTypeList } from '@/api/dish'
import type { Dish, DishType } from '@/types/dish'
import SkeletonScreen from '@/components/SkeletonScreen.vue'

const router = useRouter()

// 轮播图数据
const banners = [
  {
    id: 1,
    title: '美味佳肴，尽在餐厅订餐系统',
    desc: '新鲜食材，匠心烹饪，为您呈现舌尖上的美味',
    bg: 'linear-gradient(118deg, #351f1b 0%, #713322 48%, #c9502d 100%)'
  },
  {
    id: 2,
    title: '会员专享优惠',
    desc: '注册会员即享多重优惠，更多惊喜等你来',
    bg: 'linear-gradient(118deg, #5c2b24 0%, #a4452c 52%, #e2814e 100%)'
  },
  {
    id: 3,
    title: '品质保证，放心食用',
    desc: '严格品控，新鲜食材，让您吃得放心',
    bg: 'linear-gradient(118deg, #273c35 0%, #326b59 54%, #6da98c 100%)'
  }
]

// 分类数据
const categories = ref<DishType[]>([])
const categoriesLoading = ref(true)

// 热门菜品
const hotDishes = ref<Dish[]>([])
const dishesLoading = ref(true)

// 加载分类数据
const loadCategories = async () => {
  try {
    const res = await getDishTypeList({ current: 0, size: 8 })
    if (res.code === 200) {
      categories.value = res.data || []
    }
  } catch (error) {
    console.error('加载分类失败', error)
  } finally {
    categoriesLoading.value = false
  }
}

// 加载热门菜品
const loadHotDishes = async () => {
  try {
    const res = await getDishList({
      current: 0,
      size: 8,
      status: 1
    })
    if (res.code === 200) {
      hotDishes.value = res.data || []
    }
  } catch (error) {
    console.error('加载菜品失败', error)
  } finally {
    dishesLoading.value = false
  }
}

// 获取最低价格
const getMinPrice = (dish: Dish) => {
  if (!dish.dishesPackageList?.length) return '0.00'
  const min = Math.min(...dish.dishesPackageList.map(p => p.price))
  return min.toFixed(2)
}

// 分类点击
const handleCategoryClick = (category: DishType) => {
  router.push({
    path: '/dishes',
    query: { typeId: category.id }
  })
}

// 菜品点击
const handleDishClick = (dish: Dish) => {
  router.push({
    path: '/dishes',
    query: { dishId: dish.id }
  })
}

onMounted(() => {
  loadCategories()
  loadHotDishes()
})
</script>

<style scoped lang="scss">
.home-page {
  min-height: 100%;
  overflow: hidden;
}

.banner-section {
  padding: 20px clamp(16px, 3vw, 36px) 0;

  :deep(.el-carousel) {
    max-width: 1360px;
    margin: 0 auto;
    overflow: hidden;
    border-radius: 24px;
    box-shadow: 0 18px 42px rgba(67, 37, 26, 0.16);
  }

  :deep(.el-carousel__indicators--horizontal) {
    bottom: 16px;
  }

  :deep(.el-carousel__button) {
    width: 22px;
    height: 4px;
    border-radius: 4px;
    opacity: 0.65;
  }

  .banner-item {
    position: relative;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    padding: clamp(30px, 6vw, 80px);
    overflow: hidden;

    &::before,
    &::after {
      position: absolute;
      content: '';
      border-radius: 999px;
      pointer-events: none;
    }

    &::before {
      width: 430px;
      height: 430px;
      top: -170px;
      right: 7%;
      border: 1px solid rgba(255, 255, 255, 0.2);
      box-shadow: 0 0 0 32px rgba(255, 255, 255, 0.05), 0 0 0 72px rgba(255, 255, 255, 0.035);
    }

    &::after {
      width: 180px;
      height: 180px;
      right: 18%;
      bottom: -80px;
      background: rgba(255, 235, 218, 0.18);
    }
  }

  .banner-content {
    position: relative;
    z-index: 1;
    max-width: 620px;
    padding: 28px 30px;
    color: #fff;
    text-align: left;
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 18px;
    background: rgba(26, 15, 11, 0.16);
    backdrop-filter: blur(8px);
    animation: hero-in 0.7s var(--ease-out, ease) both;

    h2 {
      margin: 0 0 14px;
      font-size: clamp(28px, 3.1vw, 46px);
      line-height: 1.2;
      letter-spacing: 0.02em;
      text-shadow: 0 3px 18px rgba(0, 0, 0, 0.2);
    }

    p {
      max-width: 500px;
      margin: 0 0 26px;
      font-size: 16px;
      line-height: 1.8;
      opacity: 0.88;
    }

    :deep(.el-button) {
      min-height: 44px;
      padding: 0 24px;
      border: 0;
      background: #fff8f2;
      color: #a23f25;
      box-shadow: 0 9px 20px rgba(33, 18, 13, 0.16);
    }
  }
}

.section-container {
  max-width: 1360px;
  margin: 0 auto;
  padding: 72px clamp(16px, 3vw, 36px);
}

.section-title {
  margin: 0 0 38px;
  color: #30241f;
  font-size: clamp(24px, 2.2vw, 32px);
  font-weight: 750;
  letter-spacing: 0.02em;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: -10px;
    left: 0;
    width: 44px;
    height: 4px;
    border-radius: 999px;
    background: linear-gradient(90deg, #df6237, #f2b18d);
  }
}

.category-section {
  background: transparent;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(132px, 1fr));
  gap: 16px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 11px;
  min-height: 142px;
  padding: 20px 14px;
  border: 1px solid #eee3da;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  box-shadow: 0 4px 13px rgba(65, 42, 28, 0.04);
  cursor: pointer;
  transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;

  &:hover {
    border-color: #edc2ae;
    transform: translateY(-5px);
    box-shadow: 0 14px 26px rgba(118, 57, 35, 0.11);
  }

  .category-icon {
    width: 62px;
    height: 62px;
    border-radius: 50%;
  }

  .category-icon-placeholder {
    width: 64px;
    height: 64px;
    border-radius: 50%;
    border: 1px solid #f4dcd0;
    background: #fff1e9;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #c9502d;
  }

  .category-name {
    color: #473b35;
    font-size: 14px;
    font-weight: 650;
  }
}

.hot-section {
  background: #fffaf7;
}

.dish-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(245px, 1fr));
  gap: 20px;
}

.dish-card {
  background: #fff;
  border: 1px solid #eee3da;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 6px 16px rgba(65, 42, 28, 0.06);
  cursor: pointer;
  transition: transform 0.25s ease, box-shadow 0.25s ease;

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 18px 32px rgba(92, 47, 30, 0.14);
  }
}

.dish-cover {
  height: 188px;
  overflow: hidden;

  .cover-image {
    width: 100%;
    height: 100%;
    transition: transform 0.45s ease;

    &:hover {
      transform: scale(1.07);
    }
  }

  .no-cover {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #fff0e8, #f6dfd2);
    color: #a17663;
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
    margin: 0 0 12px;
    font-size: 13px;
    color: #8c8179;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .dish-bottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .dish-price {
    font-size: 20px;
    color: #c9502d;
    font-weight: 750;

    .price-suffix {
      font-size: 12px;
      color: #9a8d84;
      font-weight: normal;
    }
  }
}

.more-btn {
  margin-top: 34px;
  text-align: center;

  :deep(.el-button) {
    min-height: 40px;
    padding: 0 22px;
  }
}

.service-section {
  background: transparent;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
}

@keyframes hero-in {
  from {
    opacity: 0;
    transform: translateY(22px);
  }

  to {
    opacity: 1;
    transform: none;
  }
}

.service-item {
  text-align: center;
  padding: 30px 20px;
  border: 1px solid #eee3da;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: 0 4px 13px rgba(65, 42, 28, 0.04);
  transition: transform 0.25s ease, box-shadow 0.25s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 14px 26px rgba(118, 57, 35, 0.1);
  }

  h3 {
    margin: 16px 0 8px;
    font-size: 18px;
    color: #3b2f29;
  }

  p {
    margin: 0;
    font-size: 14px;
    color: #887c74;
  }
}

.service-icon {
  display: grid;
  width: 56px;
  height: 56px;
  margin: 0 auto;
  place-items: center;
  border-radius: 18px;
  color: #fff;
  box-shadow: 0 10px 20px rgba(65, 42, 28, 0.14);
  transition: transform 0.3s var(--ease-out, ease);
}

.service-item:hover .service-icon {
  transform: scale(1.08) rotate(-3deg);
}

.service-icon--brand {
  background: var(--gradient-brand);
}

.service-icon--jade {
  background: var(--gradient-jade);
}

.service-icon--gold {
  background: var(--gradient-gold);
}

.service-icon--deep {
  background: var(--gradient-brand-deep);
}

@media (max-width: 768px) {
  .banner-section {
    padding: 12px 12px 0;

    :deep(.el-carousel) {
      border-radius: 18px;
    }
  }

  .banner-content {
    padding: 22px;

    h2 {
      font-size: 26px;
    }

    p {
      font-size: 14px;
    }
  }

  .section-container {
    padding: 48px 18px;
  }

  .service-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 460px) {
  .category-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .service-grid {
    gap: 12px;
  }

  .service-item {
    padding: 22px 12px;
  }
}
</style>
