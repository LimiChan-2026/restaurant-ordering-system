/**
 * 菜品相关类型定义
 */

// 菜品种类
export interface DishType {
  id: number
  name: string
  iconUrl: string
}

// 菜品种类查询参数
export interface DishTypeListParams {
  current: number
  size: number
  name?: string
}

// 菜品种类新增/修改参数
export interface DishTypeSaveParams {
  id?: number
  name: string
  iconUrl: string
}

// 菜品套餐
export interface DishPackage {
  id?: number
  dishesId?: number
  name: string
  specs: string
  price: number
}

// 菜品信息
export interface Dish {
  id: number
  typeId: number
  name: string
  detail: string
  coverUrl: string
  status: boolean
  createTime?: string
  typeName?: string
  dishesPackageList?: DishPackage[]
}

// 菜品查询参数
export interface DishListParams {
  current: number
  size: number
  name?: string
  typeId?: number | null
  status?: number | null
}

// 菜品新增参数
export interface DishSaveParams {
  dishes: {
    typeId: number
    name: string
    detail: string
    coverUrl: string
    status: boolean
  }
  dishesPackageList: {
    name: string
    specs: string
    price: number
  }[]
}

// 菜品修改参数
export interface DishUpdateParams {
  id: number
  typeId?: number
  name?: string
  detail?: string
  coverUrl?: string
  status?: boolean
}

// 菜品状态修改参数
export interface DishStatusParams {
  id: number
  status: boolean
}

// 菜品套餐查询参数
export interface DishPackageListParams {
  current: number
  size: number
  dishesId?: number | null
}

// 菜品套餐新增/修改参数
export interface DishPackageSaveParams {
  id?: number
  dishesId: number
  name: string
  specs: string
  price: number
}

// 用户端菜品查询参数
export interface UserDishListParams {
  current: number
  size: number
  name?: string
  typeId?: number | null
}
