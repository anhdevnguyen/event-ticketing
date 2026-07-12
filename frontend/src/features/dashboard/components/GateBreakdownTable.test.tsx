import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { GateBreakdownTable } from './GateBreakdownTable'
import type { GateStats } from '../types'

const makeGates = (...entries: [string, number][]): GateStats[] =>
  entries.map(([gateName, checkedIn], i) => ({ gateId: i + 1, gateName, checkedIn }))

describe('GateBreakdownTable', () => {
  it('renders table section heading', () => {
    render(<GateBreakdownTable gates={[]} />)
    expect(screen.getByRole('heading', { name: /gate breakdown/i })).toBeInTheDocument()
  })

  it('renders column headers', () => {
    render(<GateBreakdownTable gates={[]} />)
    expect(screen.getByText('Gate')).toBeInTheDocument()
    expect(screen.getByText('Checked in')).toBeInTheDocument()
  })

  it('renders a row for each gate', () => {
    render(<GateBreakdownTable gates={makeGates(['Gate A', 10], ['Gate B', 5])} />)
    expect(screen.getByText('Gate A')).toBeInTheDocument()
    expect(screen.getByText('Gate B')).toBeInTheDocument()
  })

  it('renders the checked-in count for each gate', () => {
    render(<GateBreakdownTable gates={makeGates(['Gate A', 123])} />)
    expect(screen.getByText(/123/)).toBeInTheDocument()
  })

  it('formats large check-in counts with locale separators', () => {
    render(<GateBreakdownTable gates={makeGates(['Main Gate', 5000])} />)
    expect(screen.getByText(/5.000|5,000/)).toBeInTheDocument()
  })

  it('renders empty body when no gates', () => {
    const { container } = render(<GateBreakdownTable gates={[]} />)
    // Only the header row present, no data rows
    const rows = container.querySelectorAll('.gate-row:not(.gate-head)')
    expect(rows).toHaveLength(0)
  })

  it('renders zero count correctly', () => {
    render(<GateBreakdownTable gates={makeGates(['Empty Gate', 0])} />)
    expect(screen.getByText('0')).toBeInTheDocument()
  })

  it('renders multiple gates in order', () => {
    render(<GateBreakdownTable gates={makeGates(['Gate A', 10], ['Gate B', 20], ['Gate C', 30])} />)
    const cells = screen.getAllByText(/Gate [ABC]/)
    expect(cells[0]).toHaveTextContent('Gate A')
    expect(cells[1]).toHaveTextContent('Gate B')
    expect(cells[2]).toHaveTextContent('Gate C')
  })
})
