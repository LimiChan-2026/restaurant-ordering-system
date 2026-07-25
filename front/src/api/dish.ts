/**
 * 菜品相关 API
 */
import request from '@/utils/request'
import type { ApiResponse } from '@/types/user'
import type {
  DishType,
  DishTypeListParams,
  DishTypeSaveParams,
  Dish,
  DishListParams,
  DishSaveParams,
  DishUpdateParams,
  DishStatusParams,
  DishPackage,
  DishPackageListParams,
  DishPackageSaveParams
} from '@/types/dish'

// ==================== 菜品种类 ====================

/**
 * 查询菜品种类列表
 */
export function getDishTypeList(params: DishTypeListParams): Promise<ApiResponse<DishType[]>> {
  return request.post('/dishes-type/list', params)
}

/**
 * 新增菜品种类
 */
export function saveDishType(params: DishTypeSaveParams): Promise<ApiResponse<null>> {
  return request.post('/dishes-type/saveEntity', params)
}

/**
 * 修改菜品种类
 */
export function updateDishType(params: DishTypeSaveParams): Promise<ApiResponse<null>> {
  return request.put('/dishes-type/updateEntity', params)
}

/**
 * 删除菜品种类
 */
export function deleteDishType(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/dishes-type/delete/${id}`)
}

// ==================== 菜品 ====================

/**
 * 查询菜品列表
 */
export function getDishList(params: DishListParams): Promise<ApiResponse<Dish[]>> {
  return request.post('/dishes/list', params)
}

/**
 * 查询菜品详情
 */
export function getDishDetail(id: number): Promise<ApiResponse<Dish>> {
  return request.get(`/dishes/detail/${id}`)
}

/**
 * 新增菜品及套餐
 */
export function saveDish(params: DishSaveParams): Promise<ApiResponse<null>> {
  return request.post('/dishes/saveEntity', params)
}

/**
 * 修改菜品信息
 */
export function updateDish(params: DishUpdateParams): Promise<ApiResponse<null>> {
  return request.put('/dishes/updateEntity', params)
}

/**
 * 删除菜品
 */
export function deleteDish(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/dishes/delete/${id}`)
}

/**
 * 菜品上架/下架
 */
export function updateDishStatus(params: DishStatusParams): Promise<ApiResponse<null>> {
  return request.put('/dishes/updateStatus', params)
}

// ==================== 菜品套餐 ====================

/**
 * 查询菜品套餐列表
 */
export function getDishPackageList(params: DishPackageListParams): Promise<ApiResponse<DishPackage[]>> {
  return request.post('/dishes-package/list', params)
}

/**
 * 新增菜品套餐
 */
export function saveDishPackage(params: DishPackageSaveParams): Promise<ApiResponse<null>> {
  return request.post('/dishes-package/saveEntity', params)
}

/**
 * 修改菜品套餐
 */
export function updateDishPackage(params: DishPackageSaveParams): Promise<ApiResponse<null>> {
  return request.put('/dishes-package/updateEntity', params)
}

/**
 * 删除菜品套餐
 */
export function deleteDishPackage(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/dishes-package/delete/${id}`)
}
