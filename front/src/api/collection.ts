import request from '@/utils/request'
import type { ApiResponse } from '@/types/user'
import type { CollectionItem } from '@/types/evaluation'

export function toggleCollection(dishesId: number): Promise<ApiResponse<null>> { return request.post('/collection/saveOrCancel', { dishesId }) }
export function getCollectionList(): Promise<ApiResponse<CollectionItem[]>> { return request.post('/collection/listUser') }
export function isCollected(dishesId: number): Promise<ApiResponse<boolean>> { return request.get(`/collection/isCollected/${dishesId}`) }
