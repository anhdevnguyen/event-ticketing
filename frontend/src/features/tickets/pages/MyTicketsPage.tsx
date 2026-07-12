import { useCallback, useEffect, useState } from 'react'
import { QRCodeSVG } from 'qrcode.react'
import { PageTitle } from '../../../components/layout/PageTitle'
import { cancelTicket, confirmTicket, listMyTickets } from '../api/ticketApi'
import type { TicketItem } from '../types'

export function MyTicketsPage() {
  const [tickets, setTickets] = useState<TicketItem[]>([])
  const [error, setError] = useState('')

  const load = useCallback(() => {
    listMyTickets().then(setTickets).catch(() => setError('Could not load tickets.'))
  }, [])

  useEffect(load, [load])

  async function act(action: () => Promise<unknown>) {
    setError('')
    try {
      await action()
      load()
    } catch {
      setError('Ticket action failed.')
    }
  }

  return (
    <section className="page">
      <PageTitle eyebrow="Customer" title="My tickets" />
      {error && <p className="form-error">{error}</p>}
      <div className="ticket-list">
        {tickets.map((ticket) => (
          <article className="ticket-card" key={ticket.id}>
            <div>
              <strong>Ticket #{ticket.id}</strong>
              <span className={`status ${ticket.status.toLowerCase()}`}>{ticket.status}</span>
              <p>Quantity: {ticket.quantity}</p>
            </div>
            {ticket.qrCode && <QRCodeSVG value={ticket.qrCode} title={`Ticket ${ticket.id} QR`} />}
            <div className="row-actions">
              {ticket.status === 'RESERVED' && (
                <>
                  <button className="primary-action" type="button" onClick={() => void act(() => confirmTicket(ticket.id))}>Confirm</button>
                  <button className="secondary-action" type="button" onClick={() => void act(() => cancelTicket(ticket.id))}>Cancel</button>
                </>
              )}
            </div>
          </article>
        ))}
      </div>
    </section>
  )
}
