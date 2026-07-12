import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { EventsPage } from './EventsPage'
import * as eventApi from '../api/eventApi'
import * as navigation from '../../../routes/navigation'

vi.mock('../api/eventApi')
vi.mock('../../../routes/navigation', () => ({
  navigate: vi.fn(),
  usePath: vi.fn(() => '/events'),
}))

const makeEvent = (id: number, overrides = {}) => ({
  id,
  name: `Event ${id}`,
  description: `Description for event ${id}`,
  location: 'Hanoi',
  organizerId: 1,
  status: 'PUBLISHED',
  startTime: '2026-08-01T09:00:00Z',
  endTime: '2026-08-01T18:00:00Z',
  bannerUrl: null,
  createdAt: '2026-07-01T00:00:00Z',
  ...overrides,
})

beforeEach(() => {
  vi.clearAllMocks()
})

describe('EventsPage', () => {
  it('shows loading message while fetching', () => {
    vi.mocked(eventApi.listEvents).mockReturnValue(new Promise(() => { /* never resolves */ }))
    render(<EventsPage />)
    expect(screen.getByText(/loading events/i)).toBeInTheDocument()
  })

  it('renders event cards after successful fetch', async () => {
    vi.mocked(eventApi.listEvents).mockResolvedValue([makeEvent(1), makeEvent(2)])
    render(<EventsPage />)

    await waitFor(() => {
      expect(screen.getByText('Event 1')).toBeInTheDocument()
      expect(screen.getByText('Event 2')).toBeInTheDocument()
    })
  })

  it('shows the count of listed events', async () => {
    vi.mocked(eventApi.listEvents).mockResolvedValue([makeEvent(1), makeEvent(2), makeEvent(3)])
    render(<EventsPage />)

    await waitFor(() => {
      expect(screen.getByText(/3 listed/i)).toBeInTheDocument()
    })
  })

  it('shows error message when fetch fails', async () => {
    vi.mocked(eventApi.listEvents).mockRejectedValue(new Error('Network error'))
    render(<EventsPage />)

    await waitFor(() => {
      expect(screen.getByText(/could not load events/i)).toBeInTheDocument()
    })
  })

  it('shows empty state when no events', async () => {
    vi.mocked(eventApi.listEvents).mockResolvedValue([])
    render(<EventsPage />)

    await waitFor(() => {
      expect(screen.getByText(/no events found/i)).toBeInTheDocument()
    })
  })

  it('navigates to event detail when "View tickets" is clicked', async () => {
    const user = userEvent.setup()
    vi.mocked(eventApi.listEvents).mockResolvedValue([makeEvent(42)])
    render(<EventsPage />)

    await waitFor(() => screen.getByText('Event 42'))
    await user.click(screen.getByRole('button', { name: /view tickets/i }))

    expect(vi.mocked(navigation.navigate)).toHaveBeenCalledWith('/events/42')
  })

  it('renders banner image when bannerUrl is present', async () => {
    vi.mocked(eventApi.listEvents).mockResolvedValue([makeEvent(1, { bannerUrl: 'https://example.com/banner.jpg' })])
    render(<EventsPage />)

    await waitFor(() => {
      expect(screen.getByAltText(/Event 1 banner/)).toBeInTheDocument()
    })
  })

  it('renders fallback div when bannerUrl is null', async () => {
    vi.mocked(eventApi.listEvents).mockResolvedValue([makeEvent(1, { bannerUrl: null })])
    render(<EventsPage />)

    await waitFor(() => screen.getByText('Event 1'))

    const fallbacks = document.querySelectorAll('.banner-fallback')
    expect(fallbacks.length).toBeGreaterThan(0)
  })
})
