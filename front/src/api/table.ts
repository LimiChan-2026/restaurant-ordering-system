import request from '@/utils/request'
import type { ApiResponse } from '@/types/user'
import type { DishesTable, DishesTablePayload, DishesTableQuery } from '@/types/table'

export function getDishesTableList(params: DishesTableQuery): Promise<ApiResponse<DishesTable[]>> {
  return request.post('/dishes-table/list', params)
}

export function saveDishesTable(params: DishesTablePayload): Promise<ApiResponse<null>> {
  return request.post('/dishes-table/saveEntity', params)
}

export function updateDishesTable(params: DishesTablePayload): Promise<ApiResponse<null>> {
  return request.put('/dishes-table/updateEntity', params)
}

export function deleteDishesTable(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/dishes-table/delete/${id}`)
}
