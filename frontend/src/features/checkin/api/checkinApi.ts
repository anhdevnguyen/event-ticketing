import { apiClient } from '../../../lib/apiClient'
import type { CheckInResult } from '../types'

export async function checkIn(qrCode: string, gateId: number) {
  const { data } = await apiClient.post<CheckInResult>('/checkin', { qrCode, gateId })
  return data
}
