import { useEffect, useState } from 'react'
import { QRCodeSVG } from 'qrcode.react'
import { PageTitle } from '../../../components/layout/PageTitle'
import { money } from '../../../lib/format'
import { navigate } from '../../../routes/navigation'
import { confirmTicket, reserveTicket } from '../../tickets/api/ticketApi'
import type { TicketItem } from '../../tickets/types'
import { getEvent, listTicketTypes } from '../api/eventApi'
import type { EventItem, TicketTypeItem } from '../types'

interface EventDetailPageProps {
  eventId: number
  signedIn: boolean
}

export function EventDetailPage({ eventId, signedIn }: EventDetailPageProps) {
  const [event, setEvent] = useState<EventItem | null>(null)
  const [ticketTypes, setTicketTypes] = useState<TicketTypeItem[]>([])
  const [reserved, setReserved] = useState<TicketItem | null>(null)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    Promise.all([getEvent(eventId), listTicketTypes(eventId)])
      .then(([eventData, ticketData]) => {
        setEvent(eventData)
        setTicketTypes(ticketData)
      })
      .catch(() => setError('Could not load event.'))
  }, [eventId])

  async function reserve(ticketTypeId: number) {
    if (!signedIn) {
      navigate('/account')
      return
    }
    setBusy(true)
    setError('')
    try {
      setReserved(await reserveTicket(ticketTypeId, 1))
    } catch {
      setError('Could not reserve this ticket.')
    } finally {
      setBusy(false)
    }
  }

  async function confirm() {
    if (!reserved) return
    setBusy(true)
    setError('')
    try {
      setReserved(await confirmTicket(reserved.id))
    } catch {
      setError('Could not confirm this reservation.')
    } finally {
      setBusy(false)
    }
  }

  if (!event) return <p className="page-message">{error || 'Loading event...'}</p>

  return (
    <section className="page">
      <PageTitle eyebrow={event.status} title={event.name} action={<span>{event.location}</span>} />
      <section className="detail-layout">
        <article className="event-detail">
          {event.bannerUrl ? <img alt={`${event.name} banner`} src={event.bannerUrl} /> : <div className="banner-fallback large" />}
          <p>{event.description ?? 'No description yet.'}</p>
          <span>
            {new Date(event.startTime).toLocaleString()} - {new Date(event.endTime).toLocaleString()}
          </span>
        </article>
        <aside className="ticket-panel">
          <h2>Ticket types</h2>
          {ticketTypes.map((ticketType) => (
            <div className="ticket-type" key={ticketType.id}>
              <div>
                <strong>{ticketType.name}</strong>
                <span>{ticketType.quantityRemaining} remaining</span>
              </div>
              <p>{money.format(ticketType.price)}</p>
              <button className="primary-action" disabled={busy || ticketType.quantityRemaining < 1} type="button" onClick={() => void reserve(ticketType.id)}>
                Reserve
              </button>
            </div>
          ))}
          {reserved && (
            <div className="reserved-box">
              <strong>Reservation #{reserved.id}</strong>
              <span>{reserved.status}</span>
              {reserved.status === 'RESERVED' && (
                <button className="primary-action" disabled={busy} type="button" onClick={() => void confirm()}>
                  Confirm
                </button>
              )}
              {reserved.qrCode && <QRCodeSVG value={reserved.qrCode} title={`Ticket ${reserved.id} QR`} />}
            </div>
          )}
          {error && <p className="form-error">{error}</p>}
        </aside>
      </section>
    </section>
  )
}
