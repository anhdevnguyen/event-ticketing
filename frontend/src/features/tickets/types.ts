export type TicketStatus = 'RESERVED' | 'CONFIRMED' | 'CHECKED_IN' | 'EXPIRED' | 'CANCELLED'

export interface TicketItem {
  id: number
  ticketTypeId: number
  status: TicketStatus
  quantity: number
  qrCode?: string | null
  expiresAt?: string | null
  reservedAt?: string | null
  confirmedAt?: string | null
  checkedInAt?: string | null
}
