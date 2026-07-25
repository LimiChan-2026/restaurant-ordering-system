export interface CartItem {
  id: number
  userId: number
  dishesPackageId: number
  plusNumber: number
  isSelected: boolean
  createTime?: string
  dishesPackageName: string
  dishesPackagePrice: number
  dishesName: string
  dishesCover: string
}

export interface AddCartParams {
  dishesPackageId: number
  plusNumber: number
}

export interface UpdateCartParams {
  id: number
  dishesPackageId: number
  plusNumber: number
  isSelected: boolean
}
