import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MyTicketsPage } from './MyTicketsPage'
import * as ticketApi from '../api/ticketApi'
import type { TicketItem } from '../types'

vi.mock('../api/ticketApi')
vi.mock('qrcode.react', () => ({
  QRCodeSVG: ({ title }: { title: string }) => <svg data-testid="qr-code" aria-label={title} />,
}))

const makeTicket = (id: number, status: TicketItem['status'], overrides: Partial<TicketItem> = {}): TicketItem => ({
  id,
  ticketTypeId: 10,
  status,
  quantity: 1,
  qrCode: status === 'CONFIRMED' ? `qr-${id}` : null,
  expiresAt: status === 'RESERVED' ? '2026-08-01T09:15:00Z' : null,
  reservedAt: '2026-08-01T09:00:00Z',
  confirmedAt: status === 'CONFIRMED' ? '2026-08-01T09:05:00Z' : null,
  checkedInAt: status === 'CHECKED_IN' ? '2026-08-01T10:00:00Z' : null,
  ...overrides,
})

beforeEach(() => {
  vi.clearAllMocks()
})

describe('MyTicketsPage', () => {
  it('renders page title', async () => {
    vi.mocked(ticketApi.listMyTickets).mockResolvedValue([])
    render(<MyTicketsPage />)
    await waitFor(() => {
      expect(screen.getByRole('heading', { name: /my tickets/i })).toBeInTheDocument()
    })
  })

  it('renders a list of tickets', async () => {
    vi.mocked(ticketApi.listMyTickets).mockResolvedValue([
      makeTicket(1, 'RESERVED'),
      makeTicket(2, 'CONFIRMED'),
    ])
    render(<MyTicketsPage />)

    await waitFor(() => {
      expect(screen.getByText('Ticket #1')).toBeInTheDocument()
      expect(screen.getByText('Ticket #2')).toBeInTheDocument()
    })
  })

  it('shows RESERVED status badge', async () => {
    vi.mocked(ticketApi.listMyTickets).mockResolvedValue([makeTicket(1, 'RESERVED')])
    render(<MyTicketsPage />)

    await waitFor(() => {
      expect(screen.getByText('RESERVED')).toBeInTheDocument()
    })
  })

  it('shows Confirm and Cancel buttons for RESERVED ticket', async () => {
    vi.mocked(ticketApi.listMyTickets).mockResolvedValue([makeTicket(1, 'RESERVED')])
    render(<MyTicketsPage />)

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /confirm/i })).toBeInTheDocument()
      expect(screen.getByRole('button', { name: /cancel/i })).toBeInTheDocument()
    })
  })

  it('does not show action buttons for CONFIRMED ticket', async () => {
    vi.mocked(ticketApi.listMyTickets).mockResolvedValue([makeTicket(2, 'CONFIRMED')])
    render(<MyTicketsPage />)

    await waitFor(() => screen.getByText('Ticket #2'))
    expect(screen.queryByRole('button', { name: /confirm/i })).not.toBeInTheDocument()
    expect(screen.queryByRole('button', { name: /cancel/i })).not.toBeInTheDocument()
  })

  it('shows QR code for CONFIRMED ticket', async () => {
    vi.mocked(ticketApi.listMyTickets).mockResolvedValue([makeTicket(3, 'CONFIRMED')])
    render(<MyTicketsPage />)

    await waitFor(() => {
      expect(screen.getByTestId('qr-code')).toBeInTheDocument()
    })
  })

  it('reloads tickets after confirming', async () => {
    const user = userEvent.setup()
    vi.mocked(ticketApi.listMyTickets)
      .mockResolvedValueOnce([makeTicket(1, 'RESERVED')])
      .mockResolvedValueOnce([makeTicket(1, 'CONFIRMED')])
    vi.mocked(ticketApi.confirmTicket).mockResolvedValue(makeTicket(1, 'CONFIRMED'))
    render(<MyTicketsPage />)

    await waitFor(() => screen.getByRole('button', { name: /confirm/i }))
    await user.click(screen.getByRole('button', { name: /confirm/i }))

    await waitFor(() => {
      expect(ticketApi.listMyTickets).toHaveBeenCalledTimes(2)
    })
  })

  it('reloads tickets after cancelling', async () => {
    const user = userEvent.setup()
    vi.mocked(ticketApi.listMyTickets)
      .mockResolvedValueOnce([makeTicket(1, 'RESERVED')])
      .mockResolvedValueOnce([])
    vi.mocked(ticketApi.cancelTicket).mockResolvedValue(undefined)
    render(<MyTicketsPage />)

    await waitFor(() => screen.getByRole('button', { name: /cancel/i }))
    await user.click(screen.getByRole('button', { name: /cancel/i }))

    await waitFor(() => {
      expect(ticketApi.listMyTickets).toHaveBeenCalledTimes(2)
    })
  })

  it('shows error when an action fails', async () => {
    const user = userEvent.setup()
    vi.mocked(ticketApi.listMyTickets).mockResolvedValue([makeTicket(1, 'RESERVED')])
    vi.mocked(ticketApi.confirmTicket).mockRejectedValue(new Error('Conflict'))
    render(<MyTicketsPage />)

    await waitFor(() => screen.getByRole('button', { name: /confirm/i }))
    await user.click(screen.getByRole('button', { name: /confirm/i }))

    await waitFor(() => {
      expect(screen.getByText(/ticket action failed/i)).toBeInTheDocument()
    })
  })

  it('shows error when list fetch fails', async () => {
    vi.mocked(ticketApi.listMyTickets).mockRejectedValue(new Error('Network error'))
    render(<MyTicketsPage />)

    await waitFor(() => {
      expect(screen.getByText(/could not load tickets/i)).toBeInTheDocument()
    })
  })

  it('renders all ticket statuses correctly', async () => {
    vi.mocked(ticketApi.listMyTickets).mockResolvedValue([
      makeTicket(1, 'RESERVED'),
      makeTicket(2, 'CONFIRMED'),
      makeTicket(3, 'CHECKED_IN'),
      makeTicket(4, 'EXPIRED'),
      makeTicket(5, 'CANCELLED'),
    ])
    render(<MyTicketsPage />)

    await waitFor(() => {
      expect(screen.getByText('RESERVED')).toBeInTheDocument()
      expect(screen.getByText('CONFIRMED')).toBeInTheDocument()
      expect(screen.getByText('CHECKED_IN')).toBeInTheDocument()
      expect(screen.getByText('EXPIRED')).toBeInTheDocument()
      expect(screen.getByText('CANCELLED')).toBeInTheDocument()
    })
  })
})
