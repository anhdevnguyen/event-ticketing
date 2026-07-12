export interface GateStats {
  gateId: number
  gateName: string
  checkedIn: number
}

export interface DashboardSnapshot {
  eventId: number
  totalTicketsSold: number
  totalCheckedIn: number
  totalRemaining: number
  byGate: GateStats[]
}
