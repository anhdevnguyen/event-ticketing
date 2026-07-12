import type { ReactNode } from 'react'

interface PageTitleProps {
  eyebrow: string
  title: string
  action?: ReactNode
}

export function PageTitle({ eyebrow, title, action }: PageTitleProps) {
  return (
    <header className="page-title">
      <div>
        <p>{eyebrow}</p>
        <h1>{title}</h1>
      </div>
      {action}
    </header>
  )
}
