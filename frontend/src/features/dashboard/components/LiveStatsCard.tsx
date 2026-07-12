import type { ReactNode } from 'react'

interface Props {
  icon: ReactNode
  label: string
  value: number
}

export function LiveStatsCard({ icon, label, value }: Props) {
  return (
    <article className="stats-card">
      <span>{icon}</span>
      <p>{label}</p>
      <strong>{value.toLocaleString()}</strong>
    </article>
  )
}
