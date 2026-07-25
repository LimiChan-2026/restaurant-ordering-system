import request from '@/utils/request'
import type {
  LoginParams,
  RegisterParams,
  UpdateUserParams,
  AdminUpdateUserParams,
  UserListParams,
  UserInfo,
  ApiResponse,
  LoginResult
} from '@/types/user'

/**
 * 用户登录
 * @param params 登录参数 { account, password }
 */
export function login(params: LoginParams): Promise<ApiResponse<LoginResult>> {
  return request.post('/user/login', params)
}

/**
 * 用户注册
 * @param params 注册参数 { account, username, password }
 */
export function register(params: RegisterParams): Promise<ApiResponse<null>> {
  return request.post('/user/register', params)
}

/**
 * 修改用户信息（个人）
 * @param params 用户信息参数
 */
export function updateUser(params: UpdateUserParams): Promise<ApiResponse<null>> {
  return request.put('/user/updateEntity', params)
}

/**
 * 管理员修改用户信息
 * @param params 用户信息参数
 */
export function adminUpdateUser(params: AdminUpdateUserParams): Promise<ApiResponse<null>> {
  return request.put('/user/updateEntity', params)
}

/**
 * 查询用户列表（管理员）
 * @param params 查询参数
 */
export function getUserList(params: UserListParams): Promise<ApiResponse<UserInfo[]>> {
  return request.post('/user/list', params)
}

/**
 * 上传图片
 * @param file 图片文件
 */
export function uploadFile(file: File): Promise<ApiResponse<string>> {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
