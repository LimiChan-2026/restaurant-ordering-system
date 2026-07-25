import request from '@/utils/request'
export const getDishTypeCount=()=>request.get('/dashboard/dishes-type-count')
export const getOrderSales=(days=7)=>request.get('/dashboard/order-sales',{params:{days}})
export const getMerchantWallet=()=>request.get('/dashboard/merchant-wallet')
