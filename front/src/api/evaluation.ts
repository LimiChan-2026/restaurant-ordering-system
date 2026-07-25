import request from '@/utils/request'
import type { ApiResponse } from '@/types/user'
import type { Evaluation, EvaluationQuery } from '@/types/evaluation'

export function getEvaluationList(params: EvaluationQuery): Promise<ApiResponse<Evaluation[]>> { return request.post('/dishes-evaluations/list', params) }
export function saveEvaluation(params: { dishesEvaluations: { dishesId: number; content: string; ratingValue: number }; imagesList: { pictureUrl: string; number: number }[] }): Promise<ApiResponse<null>> { return request.post('/dishes-evaluations/saveEntity', params) }
export function deleteEvaluation(id: number): Promise<ApiResponse<null>> { return request.delete(`/dishes-evaluations/delete/${id}`) }
export function replyEvaluation(id: number, replyContent: string): Promise<ApiResponse<null>> { return request.put('/dishes-evaluations/reply', { id, replyContent }) }
