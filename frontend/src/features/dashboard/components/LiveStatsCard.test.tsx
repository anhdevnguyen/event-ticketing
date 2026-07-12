import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { Ticket } from 'lucide-react'
import { LiveStatsCard } from './LiveStatsCard'

describe('LiveStatsCard', () => {
  it('renders label and value', () => {
    render(<LiveStatsCard icon={<Ticket />} label="Tickets sold" value={42} />)
    expect(screen.getByText('Tickets sold')).toBeInTheDocument()
    expect(screen.getByText('42')).toBeInTheDocument()
  })

  it('renders zero value correctly', () => {
    render(<LiveStatsCard icon={<Ticket />} label="Checked in" value={0} />)
    expect(screen.getByText('0')).toBeInTheDocument()
  })

  it('formats large numbers with locale separators', () => {
    render(<LiveStatsCard icon={<Ticket />} label="Total" value={1500000} />)
    // toLocaleString() in jsdom produces "1,500,000" with en locale
    const strong = screen.getByText(/1.500.000|1,500,000/)
    expect(strong).toBeInTheDocument()
  })

  it('renders the icon', () => {
    render(<LiveStatsCard icon={<Ticket data-testid="icon" />} label="X" value={1} />)
    // Lucide renders an svg — check the span wrapper contains something
    const iconWrapper = document.querySelector('.stats-card span')
    expect(iconWrapper).not.toBeNull()
  })

  it('renders inside a stats-card article', () => {
    const { container } = render(<LiveStatsCard icon={<span />} label="X" value={5} />)
    expect(container.querySelector('article.stats-card')).not.toBeNull()
  })
})
