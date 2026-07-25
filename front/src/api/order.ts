import request from '@/utils/request'
import type { ApiResponse } from '@/types/user'
import type {
  CreateOrderParams,
  DishesTable,
  Order,
  OrderItem,
  OrderListParams,
  ShippingCarItem
} from '@/types/order'

export function createOrder(params: CreateOrderParams): Promise<ApiResponse<null>> {
  return request.post('/orders/saveEntity', params)
}

export function getOrderList(params: OrderListParams): Promise<ApiResponse<Order[]>> {
  return request.post('/orders/list', params)
}

export function getUserOrderList(params: Partial<OrderListParams> = {}): Promise<ApiResponse<Order[]>> {
  return request.post('/orders/listUser', params)
}

export function payOrder(ordersId: number): Promise<ApiResponse<null>> {
  return request.post('/orders/pay', { ordersId })
}

export function cancelOrder(ordersId: number): Promise<ApiResponse<null>> {
  return request.put('/orders/cancel', { ordersId })
}

export function serveOrder(ordersId: number): Promise<ApiResponse<null>> {
  return request.put('/orders/serve', { ordersId })
}

export function completeOrder(ordersId: number): Promise<ApiResponse<null>> {
  return request.put('/orders/complete', { ordersId })
}

export function deleteOrder(ordersId: number): Promise<ApiResponse<null>> {
  return request.delete(`/orders/delete/${ordersId}`)
}

export function getOrderItemList(ordersId: number): Promise<ApiResponse<OrderItem[]>> {
  return request.post('/orders-item/list', { ordersId })
}

// 下列调用用于订单确认页展示，均已在接口文档中定义。
export function getAvailableDishesTables(): Promise<ApiResponse<DishesTable[]>> {
  return request.post('/dishes-table/list', { current: 0, size: 100, number: '', status: true })
}

export function getSelectedCartItems(): Promise<ApiResponse<ShippingCarItem[]>> {
  return request.post('/shipping-car/listUser', { current: 0, size: 100, isSelected: true })
}

export function getWalletDetail(): Promise<ApiResponse<{ surplus: number }>> {
  return request.get('/wallet/detail')
}
