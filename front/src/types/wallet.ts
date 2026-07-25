export interface Wallet {
  id: number
  userId: number
  surplus: number
  status: boolean
  type: number
  createTime?: string
}

export interface WalletInfo {
  id: number
  walletId: number
  detail: string
  surplusMoney: number
  createTime?: string
}

export interface WalletListParams {
  current: number
  size: number
  username: string
  type: number | null
  status?: boolean | null
}

export interface WalletListItem extends Wallet {
  username?: string
}
