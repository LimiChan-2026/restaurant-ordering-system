import request from '@/utils/request'
import type { ApiResponse } from '@/types/user'
export interface MessageItem { id:number; userId:number; username?:string; content:string; type:number; readStatus:boolean; createTime:string }
export const listUserMessages=(data:{current:number;size:number;type?:number|null})=>request.post<any,ApiResponse<MessageItem[]>>('/messages/listUser',data)
export const listMessages=(data:{current:number;size:number;type?:number|null})=>request.post<any,ApiResponse<MessageItem[]>>('/messages/list',data)
export const sendMessages=(data:{userId:number;content:string}[])=>request.post('/messages/saveMessage',data)
export const readAllMessages=()=>request.put('/messages/setMessageStatus')
export const getUnreadMessageCount=()=>request.get<any,ApiResponse<number>>('/messages/unreadCount')
