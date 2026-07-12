import { apiClient } from '../../../lib/apiClient'
import type { DashboardSnapshot } from '../types'

export async function getDashboardSnapshot(eventId: number) {
  const { data } = await apiClient.get<DashboardSnapshot>(`/events/${eventId}/dashboard`)
  return data
}
