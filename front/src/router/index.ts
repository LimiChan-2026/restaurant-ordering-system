import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes: RouteRecordRaw[] = [
  // 登录/注册（不需要主布局）
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', requiresAuth: false }
  },
  // 主布局路由
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人信息', requiresAuth: true }
      },
      {
        path: 'dishes',
        name: 'DishBrowse',
        component: () => import('@/views/DishBrowse.vue'),
        meta: { title: '菜品浏览', requiresAuth: true }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/Cart.vue'),
        meta: { title: '购物车', requiresAuth: true }
      },
      {
        path: 'order-confirm',
        name: 'OrderConfirm',
        component: () => import('@/views/OrderConfirm.vue'),
        meta: { title: '确认订单', requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'UserOrders',
        component: () => import('@/views/UserOrders.vue'),
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'wallet',
        name: 'Wallet',
        component: () => import('@/views/Wallet.vue'),
        meta: { title: '我的钱包', requiresAuth: true }
      },
      { path: 'collection', name: 'Collection', component: () => import('@/views/Collection.vue'), meta: { title: '我的收藏', requiresAuth: true } },
      // 管理员路由
      { path: 'messages', name: 'Messages', component: () => import('@/views/Messages.vue'), meta: { title: '我的消息', requiresAuth: true } },
      { path: 'admin/dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '经营数据', requiresAuth: true, requiresAdmin: true } },
      { path: 'admin/refunds', name: 'RefundManagement', component: () => import('@/views/RefundManagement.vue'), meta: { title: '退款审核', requiresAuth: true, requiresAdmin: true } },
      {
        path: 'admin/users',
        name: 'UserManagement',
        component: () => import('@/views/UserManagement.vue'),
        meta: { title: '用户管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'admin/dish-types',
        name: 'DishTypeManagement',
        component: () => import('@/views/DishTypeManagement.vue'),
        meta: { title: '菜品种类管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'admin/dishes',
        name: 'DishManagement',
        component: () => import('@/views/DishManagement.vue'),
        meta: { title: '菜品管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'admin/orders',
        name: 'OrderManagement',
        component: () => import('@/views/OrderManagement.vue'),
        meta: { title: '订单管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'admin/wallets',
        name: 'WalletManagement',
        component: () => import('@/views/WalletManagement.vue'),
        meta: { title: '钱包管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'admin/tables',
        name: 'TableManagement',
        component: () => import('@/views/TableManagement.vue'),
        meta: { title: '餐桌管理', requiresAuth: true, requiresAdmin: true }
      },
      { path: 'admin/evaluations', name: 'EvaluationManagement', component: () => import('@/views/EvaluationManagement.vue'), meta: { title: '评价管理', requiresAuth: true, requiresAdmin: true } }
      ,{ path: 'admin/messages', name: 'MessageManagement', component: () => import('@/views/MessageManagement.vue'), meta: { title: '系统消息', requiresAuth: true, requiresAdmin: true } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 餐厅订餐系统`
  }

  const authStore = useAuthStore()

  // 需要登录的页面
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next({ name: 'Login' })
    return
  }

  // 需要管理员权限的页面
  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next({ name: 'Home' })
    return
  }

  // 已登录用户访问登录/注册页，重定向到首页
  if ((to.name === 'Login' || to.name === 'Register') && authStore.isLoggedIn) {
    next({ name: 'Home' })
    return
  }

  next()
})

export default router
