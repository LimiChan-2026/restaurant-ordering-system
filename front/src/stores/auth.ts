import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo } from '@/types/user'

export const useAuthStore = defineStore('auth', () => {
  const parseStoredUser = (): UserInfo | null => {
    try {
      return JSON.parse(localStorage.getItem('userInfo') || 'null')
    } catch {
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
      return null
    }
  }
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(parseStoredUser())

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 2) // role=2 是管理员
  const username = computed(() => userInfo.value?.username || '')

  // 设置登录信息
  function setLoginInfo(tokenStr: string, user: UserInfo) {
    token.value = tokenStr
    userInfo.value = user
    localStorage.setItem('token', tokenStr)
    localStorage.setItem('userInfo', JSON.stringify(user))
  }

  // 退出登录
  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 更新用户信息
  function updateUserInfo(info: Partial<UserInfo>) {
    if (userInfo.value) {
      userInfo.value = { ...userInfo.value, ...info }
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    username,
    setLoginInfo,
    logout,
    updateUserInfo
  }
})
