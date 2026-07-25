import request from '@/utils/request'
import type { ApiResponse } from '@/types/user'

export interface RefundRecord { id:number; ordersId:number; status:number; refundCause:string; rejectRefundCause?:string; refundTime?:string; createTime?:string }
export const applyRefund=(ordersId:number,refundCause:string):Promise<ApiResponse<null>>=>request.post('/orders-refund-reply/refund',{ordersId,refundCause})
export const getRefundList=(ordersId?:number):Promise<ApiResponse<RefundRecord[]>>=>request.post('/orders-refund-reply/list',ordersId?{ordersId}:{})
export const auditRefund=(id:number,status:2|3,rejectRefundCause?:string):Promise<ApiResponse<null>>=>request.put('/orders-refund-reply/audit',{id,status,rejectRefundCause})
