<template>
  <div class="main-layout">
    <!-- 顶部导航栏 -->
    <header class="header" :class="{ 'is-scrolled': isScrolled }">
      <div class="header-content">
        <!-- Logo -->
        <div class="logo" @click="router.push('/')">
          <img class="logo-icon" src="http://localhost:8081/uploads/app-icon.png" alt="餐厅订餐系统图标" />
          <span class="logo-text">餐厅订餐系统</span>
        </div>

        <!-- 导航菜单 -->
        <nav class="nav-menu">
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            :ellipsis="false"
            @select="handleMenuSelect"
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/dishes">菜品浏览</el-menu-item>
            <el-menu-item index="/cart" v-if="!authStore.isAdmin">
              <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
                购物车
              </el-badge>
            </el-menu-item>
            <el-menu-item index="/orders" v-if="!authStore.isAdmin">我的订单</el-menu-item>
            <template v-if="authStore.isAdmin">
              <el-menu-item index="/admin/orders">
                <el-badge :value="pendingOrderCount" :hidden="pendingOrderCount === 0" class="admin-nav-badge">订单管理</el-badge>
              </el-menu-item>
              <el-menu-item index="/admin/refunds">
                <el-badge :value="pendingRefundCount" :hidden="pendingRefundCount === 0" class="admin-nav-badge">退款审核</el-badge>
              </el-menu-item>
              <el-menu-item index="/admin/dashboard">运营数据</el-menu-item>
            </template>
            <el-sub-menu index="admin-menu" v-if="authStore.isAdmin">
              <template #title>管理中心</template>
              <el-menu-item index="/admin/dishes">菜品管理</el-menu-item>
              <el-menu-item index="/admin/dish-types">菜品分类</el-menu-item>
              <el-menu-item index="/admin/tables">餐桌管理</el-menu-item>
              <el-menu-item index="/admin/evaluations">评价管理</el-menu-item>
              <el-menu-item index="/admin/wallets">钱包管理</el-menu-item>
              <el-menu-item index="/admin/users">用户管理</el-menu-item>
              <el-menu-item index="/admin/messages">系统消息</el-menu-item>
            </el-sub-menu>
          </el-menu>
        </nav>

        <!-- 用户区域 -->
        <div class="user-area">
          <template v-if="authStore.isLoggedIn">
            <el-popover v-if="authStore.isAdmin" placement="bottom-end" :width="244" trigger="click">
              <template #reference>
                <el-badge :value="pendingWorkCount" :hidden="pendingWorkCount === 0" :max="99" class="admin-reminder-badge">
                  <el-button circle aria-label="待处理提醒"><el-icon><Bell /></el-icon></el-button>
                </el-badge>
              </template>
              <div class="admin-reminder-panel">
                <p class="admin-reminder-title">待处理提醒</p>
                <button type="button" class="admin-reminder-item" @click="router.push('/admin/orders')">
                  <span>新订单（待出餐）</span><strong>{{ pendingOrderCount }}</strong>
                </button>
                <button type="button" class="admin-reminder-item" @click="router.push('/admin/refunds')">
                  <span>待审核退款</span><strong>{{ pendingRefundCount }}</strong>
                </button>
              </div>
            </el-popover>
            <el-dropdown @command="handleUserCommand">
              <div class="user-info">
                <el-avatar :size="32" :src="authStore.userInfo?.avatar" class="user-avatar">
                  {{ authStore.username?.charAt(0) || 'U' }}
                </el-avatar>
                <span class="username">{{ authStore.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人信息
                  </el-dropdown-item>
                  <el-dropdown-item command="wallet" v-if="!authStore.isAdmin">
                    <el-icon><Wallet /></el-icon>
                    我的钱包
                  </el-dropdown-item>
                  <el-dropdown-item command="collection" v-if="!authStore.isAdmin">
                    <el-icon><Star /></el-icon>
                    我的收藏
                  </el-dropdown-item>
                  <el-dropdown-item command="messages">
                    <el-badge :value="unreadMessageCount" :hidden="unreadMessageCount === 0"><el-icon><Bell /></el-icon></el-badge>
                    我的消息
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" @click="router.push('/login')">登录</el-button>
            <el-button @click="router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <Transition name="page" mode="out-in">
          <component :is="Component" />
        </Transition>
      </router-view>
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-grid">
          <div class="footer-brand">
            <div class="footer-logo">
              <span class="footer-logo-icon">🍽️</span>餐厅订餐系统
            </div>
            <p class="footer-slogan">新鲜食材 · 匠心烹饪 · 极速送达</p>
          </div>
          <nav class="footer-links">
            <p class="footer-title">快速入口</p>
            <router-link to="/dishes">菜品浏览</router-link>
            <router-link to="/cart">购物车</router-link>
            <router-link to="/orders">我的订单</router-link>
          </nav>
          <nav class="footer-links">
            <p class="footer-title">服务支持</p>
            <router-link to="/messages">我的消息</router-link>
            <router-link to="/profile">个人中心</router-link>
            <router-link to="/wallet">我的钱包</router-link>
          </nav>
        </div>
        <div class="footer-bottom">© 2026 餐厅订餐系统 - 提供优质餐饮服务</div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown, Bell, User, Wallet, Star, SwitchButton } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { getCartItems } from '@/api/cart'
import { getUnreadMessageCount } from '@/api/message'
import { getOrderList } from '@/api/order'
import { getRefundList } from '@/api/refund'
import { HEADER_INDICATOR_CHANGED } from '@/utils/headerIndicator'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const cartCount = ref(0)
const unreadMessageCount = ref(0)
const pendingOrderCount = ref(0)
const pendingRefundCount = ref(0)
const isScrolled = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 8
}
const pendingWorkCount = computed(() => pendingOrderCount.value + pendingRefundCount.value)

const loadCartCount = async () => {
  if (!authStore.isLoggedIn || authStore.isAdmin) {
    cartCount.value = 0
    return
  }
  try {
    const res = await getCartItems()
    cartCount.value = res.data?.length || 0
  } catch {
    cartCount.value = 0
  }
}

// 当前激活的菜单
const loadUnreadMessageCount = async () => {
  if (!authStore.isLoggedIn) { unreadMessageCount.value = 0; return }
  try {
    const res = await getUnreadMessageCount()
    unreadMessageCount.value = Number(res.data || 0)
  } catch { unreadMessageCount.value = 0 }
}

const loadAdminPendingCounts = async () => {
  if (!authStore.isLoggedIn || !authStore.isAdmin) {
    pendingOrderCount.value = 0
    pendingRefundCount.value = 0
    return
  }
  try {
    const [ordersRes, refundsRes] = await Promise.all([
      getOrderList({ current: 0, size: 1, status: 2, code: '' }),
      getRefundList()
    ])
    pendingOrderCount.value = Number(ordersRes.count || 0)
    pendingRefundCount.value = (refundsRes.data || []).filter(item => item.status === 1).length
  } catch {
    pendingOrderCount.value = 0
    pendingRefundCount.value = 0
  }
}

const refreshHeaderIndicators = () => {
  loadUnreadMessageCount()
  loadAdminPendingCounts()
}

const activeMenu = computed(() => {
  return route.path
})

// 菜单选择
const handleMenuSelect = (index: string) => {
  router.push(index)
}

// 用户菜单命令
const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'wallet':
      router.push('/wallet')
      break
    case 'collection':
      router.push('/collection')
      break
      case 'messages':
        router.push('/messages')
        break
      case 'logout':
      authStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
      break
  }
}

let unreadMessageTimer: number | undefined

watch(() => route.path, () => { loadCartCount(); loadUnreadMessageCount(); loadAdminPendingCounts() })
onMounted(() => {
  loadCartCount()
  refreshHeaderIndicators()
  handleScroll()
  window.addEventListener('scroll', handleScroll, { passive: true })
  window.addEventListener(HEADER_INDICATOR_CHANGED, refreshHeaderIndicators)
  unreadMessageTimer = window.setInterval(() => {
    refreshHeaderIndicators()
  }, 30000)
})
onUnmounted(() => {
  if (unreadMessageTimer !== undefined) window.clearInterval(unreadMessageTimer)
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener(HEADER_INDICATOR_CHANGED, refreshHeaderIndicators)
})
</script>

<style scoped lang="scss">
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 68px;
  border-bottom: 1px solid rgba(238, 229, 220, 0.9);
  background: rgba(255, 253, 251, 0.9);
  box-shadow: 0 4px 20px rgba(65, 42, 28, 0.06);
  backdrop-filter: blur(16px);
  z-index: 1000;
  transition: box-shadow 0.25s ease, background-color 0.25s ease;

  &.is-scrolled {
    background: rgba(255, 253, 251, 0.97);
    box-shadow: 0 8px 28px rgba(65, 42, 28, 0.12);
  }
}

.header-content {
  max-width: 1360px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 28px;
  box-sizing: border-box;
  min-width: 0;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 28px;
  flex: 0 0 auto;
  white-space: nowrap;

  .logo-icon {
    display: block;
    width: 38px;
    height: 38px;
    margin-right: 10px;
    border-radius: 12px;
    object-fit: cover;
    box-shadow: 0 7px 14px rgba(201, 80, 45, 0.22);
  }

  .logo-text {
    color: #32241f;
    font-size: 17px;
    font-weight: 750;
    letter-spacing: 0.02em;
  }
}

.nav-menu {
  flex: 1 1 auto;
  min-width: 0;
  overflow-x: auto;
  overflow-y: hidden;
  scrollbar-width: none;

  .el-menu {
    --el-menu-bg-color: transparent;
    --el-menu-hover-bg-color: #fff1ea;
    --el-menu-active-color: #c9502d;
    border-bottom: none;
    width: max-content;
    min-width: 100%;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    height: 68px;
    padding: 0 13px;
    color: #665d57;
    font-size: 14px;
    font-weight: 600;
    white-space: nowrap;
  }

  :deep(.el-menu-item.is-active) {
    color: #b74627;
    font-weight: 700;
  }

  :deep(.el-menu-item.is-active::after) {
    right: 13px;
    left: 13px;
    height: 3px;
    border-radius: 3px 3px 0 0;
    background: #df6237;
  }

  .cart-badge {
    :deep(.el-badge__content) {
      top: -2px;
      right: -8px;
    }
  }

  .admin-nav-badge {
    :deep(.el-badge__content) {
      top: 4px;
      right: 14px;
    }
  }
}

.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 0 0 auto;
  margin-left: 14px;

  :deep(.el-button) {
    min-height: 34px;
    padding-right: 14px;
    padding-left: 14px;
  }

  .admin-reminder-badge {
    display: inline-flex;

    :deep(.el-badge__content) {
      top: 1px;
      right: 5px;
    }
  }
}

.admin-reminder-panel {
  padding: 3px 1px;
}

.admin-reminder-title {
  margin: 2px 8px 8px;
  color: #493f3a;
  font-size: 14px;
  font-weight: 700;
}

.admin-reminder-item {
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: space-between;
  padding: 10px 8px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: #665d57;
  cursor: pointer;
  font: inherit;
  text-align: left;

  &:hover {
    background: #fff5ef;
  }

  strong {
    display: grid;
    min-width: 22px;
    height: 22px;
    padding: 0 6px;
    box-sizing: border-box;
    place-items: center;
    border-radius: 11px;
    background: #c9502d;
    color: #fff;
    font-size: 12px;
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 9px;
  cursor: pointer;
  padding: 5px 9px 5px 5px;
  border: 1px solid transparent;
  border-radius: 999px;
  transition: background-color 0.25s, border-color 0.25s;

  &:hover {
    border-color: #f0dfd4;
    background-color: #fff5ef;
  }

  .user-avatar {
    background: linear-gradient(135deg, #c9502d 0%, #e77b4e 100%);
    color: #fff;
    font-size: 14px;
  }

  .username {
    font-size: 14px;
    color: #493f3a;
    font-weight: 600;
  }
}

.main-content {
  flex: 1;
  margin-top: 68px;
}

.footer {
  border-top: 1px solid #eadfd6;
  background:
    radial-gradient(circle at 85% 0, rgba(223, 98, 55, 0.14), transparent 22rem),
    #312721;
  color: #eadfd6;
}

.footer-content {
  max-width: 1360px;
  margin: 0 auto;
  width: 100%;
  padding: 38px 28px 20px;
  box-sizing: border-box;
}

.footer-grid {
  display: grid;
  grid-template-columns: 1.6fr 1fr 1fr;
  gap: 28px;
  padding-bottom: 26px;
  border-bottom: 1px solid rgba(234, 223, 214, 0.14);
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff7f0;
  font-size: 17px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.footer-logo-icon {
  font-size: 20px;
}

.footer-slogan {
  margin: 10px 0 0;
  color: rgba(234, 223, 214, 0.66);
  font-size: 13px;
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: 9px;

  .footer-title {
    margin: 0 0 4px;
    color: #fff7f0;
    font-size: 14px;
    font-weight: 700;
  }

  a {
    width: fit-content;
    color: rgba(234, 223, 214, 0.72);
    font-size: 13px;
    transition: color 0.2s ease, transform 0.2s ease;

    &:hover {
      color: #ffbfa1;
      transform: translateX(3px);
    }
  }
}

.footer-bottom {
  padding-top: 18px;
  text-align: center;
  color: rgba(234, 223, 214, 0.5);
  font-size: 12.5px;
  letter-spacing: 0.02em;
}

@media (max-width: 900px) {
  .header-content {
    padding: 0 16px;
  }

  .logo {
    margin-right: 12px;
  }

  .user-area {
    margin-left: 8px;
  }
}

@media (max-width: 640px) {
  .header {
    height: 60px;
  }

  .main-content {
    margin-top: 60px;
  }

  .footer-content {
    padding: 30px 18px 16px;
  }

  .footer-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .logo {
    margin-right: 6px;

    .logo-text {
      display: none;
    }
  }

  .user-area {
    gap: 6px;
    margin-left: 4px;

    :deep(.el-button:not(.el-button--primary)) {
      display: none;
    }
  }

  .user-info {
    padding: 4px;

    .username {
      display: none;
    }
  }

  .nav-menu {
    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      height: 60px;
      padding: 0 9px;
      font-size: 13px;
    }
  }
}
</style>
