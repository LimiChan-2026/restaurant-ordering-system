export interface DishesTable {
  id: number
  number: string
  personNumber: number
  status: boolean
  occupied: boolean
}

export interface DishesTableQuery {
  current: number
  size: number
  number: string
  status: boolean | null
}

export interface DishesTablePayload {
  id?: number
  number: string
  personNumber: number
  status: boolean
}
