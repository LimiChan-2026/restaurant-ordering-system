export interface EvaluationImage { id?: number; pictureUrl: string; number: number }
export interface Evaluation {
  id: number; dishesId: number; userId: number; content: string; ratingValue: number; replyContent: string | null; replyStatus: boolean; createTime?: string
  dishesName?: string; dishesCover?: string; username?: string; avatar?: string; imagesList?: EvaluationImage[]
}
export interface EvaluationQuery { current: number; size: number; dishesId?: number | null; userId?: number | null }
export interface CollectionItem { id: number; dishesId: number; dishesName: string; dishesDetail?: string; dishesCover?: string; dishesStatus: boolean }
