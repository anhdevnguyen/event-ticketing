import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { CheckInPage } from './CheckInPage'
import * as eventApi from '../../events/api/eventApi'
import * as checkinApi from '../api/checkinApi'

vi.mock('../../events/api/eventApi')
vi.mock('../api/checkinApi')
// QrScanner uses html5-qrcode which requires a real camera; mock it
vi.mock('../components/QrScanner', () => ({
  QrScanner: ({ onScan }: { onScan: (code: string) => void }) => (
    <button data-testid="mock-scanner" onClick={() => onScan('scanned-qr-code')}>
      Simulate scan
    </button>
  ),
}))

const mockEvents = [
  { id: 1, name: 'Event Alpha', description: null, location: 'Hanoi', organizerId: 1, status: 'PUBLISHED', startTime: '2026-08-01T09:00:00Z', endTime: '2026-08-01T18:00:00Z', bannerUrl: null, createdAt: '' },
  { id: 2, name: 'Event Beta', description: null, location: 'HCM', organizerId: 1, status: 'PUBLISHED', startTime: '2026-09-01T09:00:00Z', endTime: '2026-09-01T18:00:00Z', bannerUrl: null, createdAt: '' },
]

const mockGates = [
  { id: 101, eventId: 1, name: 'Gate A' },
  { id: 102, eventId: 1, name: 'Gate B' },
]

const mockCheckInResult = {
  ticketId: 99,
  status: 'CHECKED_IN',
  checkedInAt: '2026-08-01T10:30:00Z',
  gateId: 101,
}

beforeEach(() => {
  vi.clearAllMocks()
  vi.mocked(eventApi.listEvents).mockResolvedValue(mockEvents)
  vi.mocked(eventApi.listGates).mockResolvedValue(mockGates)
})

describe('CheckInPage', () => {
  it('renders event and gate selects after loading', async () => {
    render(<CheckInPage />)

    await waitFor(() => {
      expect(screen.getByDisplayValue('Event Alpha')).toBeInTheDocument()
      expect(screen.getByDisplayValue('Gate A')).toBeInTheDocument()
    })
  })

  it('renders QR manual input form', async () => {
    render(<CheckInPage />)
    await waitFor(() => screen.getByDisplayValue('Event Alpha'))

    expect(screen.getByLabelText(/qr code/i)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /check in/i })).toBeInTheDocument()
  })

  it('shows success result after successful manual check-in', async () => {
    const user = userEvent.setup()
    vi.mocked(checkinApi.checkIn).mockResolvedValue(mockCheckInResult)
    render(<CheckInPage />)
    await waitFor(() => screen.getByDisplayValue('Gate A'))

    await user.type(screen.getByLabelText(/qr code/i), 'some-qr-code')
    await user.click(screen.getByRole('button', { name: /check in/i }))

    await waitFor(() => {
      expect(screen.getByText(/check-in successful/i)).toBeInTheDocument()
      expect(screen.getByText(/ticket #99/i)).toBeInTheDocument()
      expect(screen.getByText(/gate #101/i)).toBeInTheDocument()
    })
  })

  it('shows error result when check-in fails', async () => {
    const user = userEvent.setup()
    vi.mocked(checkinApi.checkIn).mockRejectedValue(new Error('Duplicate'))
    render(<CheckInPage />)
    await waitFor(() => screen.getByDisplayValue('Gate A'))

    await user.type(screen.getByLabelText(/qr code/i), 'duplicate-qr')
    await user.click(screen.getByRole('button', { name: /check in/i }))

    await waitFor(() => {
      // The error section has class="checkin-result error"
      const errorSection = document.querySelector('.checkin-result.error')
      expect(errorSection).not.toBeNull()
      expect(errorSection).toHaveTextContent(/check-in failed/i)
    })
  })

  it('clears QR input after successful check-in', async () => {
    const user = userEvent.setup()
    vi.mocked(checkinApi.checkIn).mockResolvedValue(mockCheckInResult)
    render(<CheckInPage />)
    await waitFor(() => screen.getByDisplayValue('Gate A'))

    const input = screen.getByLabelText(/qr code/i)
    await user.type(input, 'some-qr-code')
    await user.click(screen.getByRole('button', { name: /check in/i }))

    await waitFor(() => {
      expect(input).toHaveValue('')
    })
  })

  it('does not submit when QR input is empty', async () => {
    const user = userEvent.setup()
    render(<CheckInPage />)
    await waitFor(() => screen.getByDisplayValue('Gate A'))

    await user.click(screen.getByRole('button', { name: /check in/i }))

    expect(vi.mocked(checkinApi.checkIn)).not.toHaveBeenCalled()
  })

  it('calls checkIn with correct gateId when gate is selected', async () => {
    const user = userEvent.setup()
    vi.mocked(checkinApi.checkIn).mockResolvedValue(mockCheckInResult)
    render(<CheckInPage />)
    await waitFor(() => screen.getByDisplayValue('Gate A'))

    // Gate select is the second combobox (after event select)
    const [, gateSelect] = screen.getAllByRole('combobox')
    await user.selectOptions(gateSelect, '102')
    await user.type(screen.getByLabelText(/qr code/i), 'qr-xyz')
    await user.click(screen.getByRole('button', { name: /check in/i }))

    await waitFor(() => {
      expect(vi.mocked(checkinApi.checkIn)).toHaveBeenCalledWith('qr-xyz', 102)
    })
  })

  it('triggers check-in when QR Scanner fires a scan', async () => {
    const user = userEvent.setup()
    vi.mocked(checkinApi.checkIn).mockResolvedValue(mockCheckInResult)
    render(<CheckInPage />)
    await waitFor(() => screen.getByDisplayValue('Gate A'))

    await user.click(screen.getByTestId('mock-scanner'))

    await waitFor(() => {
      expect(vi.mocked(checkinApi.checkIn)).toHaveBeenCalledWith('scanned-qr-code', 101)
      expect(screen.getByText(/check-in successful/i)).toBeInTheDocument()
    })
  })

  it('loads gates for newly selected event', async () => {
    const user = userEvent.setup()
    const mockGatesForEvent2 = [{ id: 201, eventId: 2, name: 'Gate X' }]
    vi.mocked(eventApi.listGates)
      .mockResolvedValueOnce(mockGates)          // for event 1
      .mockResolvedValueOnce(mockGatesForEvent2) // for event 2
    render(<CheckInPage />)
    await waitFor(() => screen.getByDisplayValue('Event Alpha'))

    await user.selectOptions(screen.getAllByRole('combobox')[0], '2')

    await waitFor(() => {
      expect(screen.getByDisplayValue('Gate X')).toBeInTheDocument()
    })
  })
})
