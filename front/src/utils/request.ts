import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types/user'

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data

    // 根据接口文档，code 200 表示成功
    if (res.code === 200) {
      return res as any
    }

    if (res.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }

    // 处理业务错误
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    // 处理 HTTP 错误
    if (error.response) {
      const { status, data } = error.response
      const responseMessage = data?.message
      switch (status) {
        case 401:
          ElMessage.error(responseMessage || '未授权，请重新登录')
          // 清除 token 并跳转到登录页
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          if (window.location.pathname !== '/login') window.location.href = '/login'
          break
        case 403:
          ElMessage.error(responseMessage || '无操作权限')
          break
        case 404:
          ElMessage.error(responseMessage || '请求的资源不存在')
          break
        case 500:
          ElMessage.error(responseMessage || '服务器内部错误')
          break
        default:
          ElMessage.error(responseMessage || error.message || '网络错误')
      }
    } else {
      ElMessage.error('网络连接失败，请检查网络')
    }
    return Promise.reject(error)
  }
)

export default service
