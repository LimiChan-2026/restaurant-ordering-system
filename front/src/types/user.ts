// 用户信息类型
export interface UserInfo {
  id: number
  account: string
  username: string
  avatar: string
  role: number // 1: 普通用户, 2: 管理员
  gender?: number
  birthday?: string
  phone?: string
  email?: string
  status?: boolean // true: 正常, false: 禁用
  createTime?: string
}

// 登录请求参数
export interface LoginParams {
  account: string
  password: string
}

// 注册请求参数
export interface RegisterParams {
  username: string
  password: string
  confirmPassword: string
}

// 登录响应数据
export interface LoginResult {
  token: string
  user: UserInfo
}

// 修改用户信息参数
export interface UpdateUserParams {
  id: number
  username: string
  avatar: string
  gender: number // 0: 未知, 1: 男, 2: 女
  birthday: string
  phone: string
  email: string
}

// 管理员修改用户参数
export interface AdminUpdateUserParams {
  id: number
  username?: string
  role?: number
  status?: boolean
}

// 查询用户列表参数
export interface UserListParams {
  current: number
  size: number
  username?: string
  role?: number | null
}

// 通用响应格式
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  count?: number
}
