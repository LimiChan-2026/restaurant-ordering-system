import request from '@/utils/request'
import type { ApiResponse } from '@/types/user'
import type { Wallet, WalletInfo, WalletListItem, WalletListParams } from '@/types/wallet'

export function getWalletDetail(): Promise<ApiResponse<Wallet>> {
  return request.get('/wallet/detail')
}

export function rechargeWallet(money: number): Promise<ApiResponse<null>> {
  return request.post('/wallet/recharge', { money })
}

export function getWalletInfoList(walletId: number): Promise<ApiResponse<WalletInfo[]>> {
  return request.post('/wallet-info/list', { current: 0, size: 100, walletId })
}

export function getWalletList(params: WalletListParams): Promise<ApiResponse<WalletListItem[]>> {
  return request.post('/wallet/list', params)
}
