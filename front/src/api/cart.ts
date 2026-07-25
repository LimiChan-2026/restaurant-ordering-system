import request from '@/utils/request'
import type { ApiResponse } from '@/types/user'
import type { AddCartParams, CartItem, UpdateCartParams } from '@/types/cart'

export function addCartItem(params: AddCartParams): Promise<ApiResponse<null>> {
  return request.post('/shipping-car/saveEntity', params)
}

export function getCartItems(isSelected: boolean | null = null): Promise<ApiResponse<CartItem[]>> {
  return request.post('/shipping-car/listUser', { current: 0, size: 100, isSelected })
}

export function updateCartItem(params: UpdateCartParams): Promise<ApiResponse<null>> {
  return request.put('/shipping-car/updateEntity', params)
}

export function deleteCartItem(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/shipping-car/delete/${id}`)
}
