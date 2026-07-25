/** 订单模块类型定义（字段与接口文档保持一致） */

export interface OrderItemDTO {
  dishesPackageId: number
  buyNumber: number
}

export interface CreateOrderParams {
  dishesTableId: number
  orderItemDtoList: OrderItemDTO[]
}

export interface OrderListParams {
  current: number
  size: number
  status: number | null
  code: string
}

export interface Order {
  id: number
  code: string
  userId: number
  dishesTableId: number
  status: number
  totalPrice: number
  serveFoodTime?: string
  payTime?: string
  createTime?: string
  username?: string
  userPhone?: string
  dishesTableNumber?: string
}

export interface OrderItem {
  id: number
  ordersId: number
  dishesPackageId: number
  snapPrice: number
  snapName: string
  snapCover: string
  buyNumber: number
  totalPrice: number
  createTime?: string
}

export interface DishesTable {
  id: number
  number: string
  personNumber: number
  status: boolean
  occupied: boolean
}

export interface ShippingCarItem {
  id: number
  dishesPackageId: number
  plusNumber: number
  isSelected: boolean
  dishesPackageName: string
  dishesPackagePrice: number
  dishesName: string
  dishesCover: string
}
