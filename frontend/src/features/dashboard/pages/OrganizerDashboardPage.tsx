import { useCallback, useEffect, useState, type FormEvent } from 'react'
import { Activity, CalendarPlus, DoorOpen, RotateCw, Ticket, TicketCheck, WalletCards } from 'lucide-react'
import { PageTitle } from '../../../components/layout/PageTitle'
import { money } from '../../../lib/format'
import { createEvent, createGate, createTicketType, listEvents, listGates, listTicketTypes } from '../../events/api/eventApi'
import type { EventItem, GateItem, TicketTypeItem } from '../../events/types'
import { getDashboardSnapshot } from '../api/dashboardApi'
import { GateBreakdownTable } from '../components/GateBreakdownTable'
import { LiveStatsCard } from '../components/LiveStatsCard'
import { useDashboardSocket } from '../hooks/useDashboardSocket'
import type { DashboardSnapshot } from '../types'

export function OrganizerDashboardPage({ accessToken }: { accessToken: string | null }) {
  const [events, setEvents] = useState<EventItem[]>([])
  const [eventId, setEventId] = useState<number | null>(null)
  const [snapshot, setSnapshot] = useState<DashboardSnapshot | null>(null)
  const [ticketTypes, setTicketTypes] = useState<TicketTypeItem[]>([])
  const [gates, setGates] = useState<GateItem[]>([])
  const [error, setError] = useState('')

  const loadEvents = useCallback(() => {
    listEvents().then((items) => {
      setEvents(items)
      setEventId((current) => current ?? items[0]?.id ?? null)
    }).catch(() => setError('Could not load events.'))
  }, [])

  useEffect(loadEvents, [loadEvents])

  const loadEventData = useCallback(() => {
    if (!eventId) return
    Promise.all([getDashboardSnapshot(eventId), listTicketTypes(eventId), listGates(eventId)])
      .then(([snapshotData, ticketData, gateData]) => {
        setSnapshot(snapshotData)
        setTicketTypes(ticketData)
        setGates(gateData)
      })
      .catch(() => setError('Could not load dashboard.'))
  }, [eventId])

  useEffect(loadEventData, [loadEventData])

  const connected = useDashboardSocket(eventId, accessToken, loadEventData)
  const event = events.find((item) => item.id === eventId)

  return (
    <section className="page">
      <PageTitle
        eyebrow="Organizer dashboard"
        title={event?.name ?? 'Events'}
        action={
          <select value={eventId ?? ''} onChange={(change) => setEventId(Number(change.target.value))}>
            {events.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}
          </select>
        }
      />
      <section className="live-strip">
        <span className={connected ? 'live-dot on' : 'live-dot'} />
        <strong>{connected ? 'Live updates connected' : 'Waiting for live updates'}</strong>
        <button type="button" onClick={loadEventData}><RotateCw size={16} /></button>
      </section>
      {error && <p className="form-error">{error}</p>}
      {snapshot && (
        <>
          <section className="stats-grid">
            <LiveStatsCard icon={<WalletCards size={22} />} label="Tickets sold" value={snapshot.totalTicketsSold} />
            <LiveStatsCard icon={<TicketCheck size={22} />} label="Checked in" value={snapshot.totalCheckedIn} />
            <LiveStatsCard icon={<Ticket size={22} />} label="Remaining" value={snapshot.totalRemaining} />
            <LiveStatsCard icon={<DoorOpen size={22} />} label="Open gates" value={snapshot.byGate.length} />
          </section>
          <section className="dashboard-main">
            <GateBreakdownTable gates={snapshot.byGate} />
            <section className="dashboard-section">
              <h2>Event flow</h2>
              <div className="flow-meter">
                <Activity size={22} />
                <strong>{snapshot.totalTicketsSold ? Math.round((snapshot.totalCheckedIn / snapshot.totalTicketsSold) * 100) : 0}%</strong>
                <span>of sold tickets checked in</span>
              </div>
            </section>
          </section>
        </>
      )}
      <section className="manage-grid">
        <EventCreateForm onCreated={loadEvents} />
        {eventId && <TicketTypeCreateForm eventId={eventId} onCreated={loadEventData} />}
        {eventId && <GateCreateForm eventId={eventId} onCreated={loadEventData} />}
      </section>
      <section className="management-list">
        <h2>Ticket types</h2>
        {ticketTypes.map((ticketType) => (
          <p key={ticketType.id}>{ticketType.name} · {money.format(ticketType.price)} · {ticketType.quantityRemaining}/{ticketType.quantityTotal}</p>
        ))}
        <h2>Gates</h2>
        {gates.map((gate) => <p key={gate.id}>{gate.name}</p>)}
      </section>
    </section>
  )
}

function EventCreateForm({ onCreated }: { onCreated: () => void }) {
  const [error, setError] = useState('')

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    setError('')
    try {
      await createEvent({
        name: String(data.get('name')),
        description: String(data.get('description')),
        location: String(data.get('location')),
        startTime: new Date(String(data.get('startTime'))).toISOString(),
        endTime: new Date(String(data.get('endTime'))).toISOString(),
      })
      event.currentTarget.reset()
      onCreated()
    } catch {
      setError('Could not create event.')
    }
  }

  return (
    <form className="panel-form" onSubmit={submit}>
      <h2>Create event</h2>
      <input name="name" placeholder="Event name" required />
      <input name="location" placeholder="Location" required />
      <textarea name="description" placeholder="Description" required />
      <input name="startTime" type="datetime-local" required />
      <input name="endTime" type="datetime-local" required />
      {error && <p className="form-error">{error}</p>}
      <button className="primary-action" type="submit"><CalendarPlus size={18} />Create</button>
    </form>
  )
}

function TicketTypeCreateForm({ eventId, onCreated }: { eventId: number; onCreated: () => void }) {
  const [error, setError] = useState('')

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    setError('')
    try {
      await createTicketType(eventId, {
        name: String(data.get('name')),
        price: Number(data.get('price')),
        quantityTotal: Number(data.get('quantityTotal')),
        salesStartAt: new Date(String(data.get('salesStartAt'))).toISOString(),
        salesEndAt: new Date(String(data.get('salesEndAt'))).toISOString(),
      })
      event.currentTarget.reset()
      onCreated()
    } catch {
      setError('Could not create ticket type.')
    }
  }

  return (
    <form className="panel-form" onSubmit={submit}>
      <h2>Create ticket type</h2>
      <input name="name" placeholder="Ticket type" required />
      <input min="0" name="price" placeholder="Price" required type="number" />
      <input min="1" name="quantityTotal" placeholder="Quantity" required type="number" />
      <input name="salesStartAt" type="datetime-local" required />
      <input name="salesEndAt" type="datetime-local" required />
      {error && <p className="form-error">{error}</p>}
      <button className="primary-action" type="submit">Create</button>
    </form>
  )
}

function GateCreateForm({ eventId, onCreated }: { eventId: number; onCreated: () => void }) {
  const [error, setError] = useState('')

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    setError('')
    try {
      await createGate(eventId, String(data.get('name')))
      event.currentTarget.reset()
      onCreated()
    } catch {
      setError('Could not create gate.')
    }
  }

  return (
    <form className="panel-form" onSubmit={submit}>
      <h2>Create gate</h2>
      <input name="name" placeholder="Gate name" required />
      {error && <p className="form-error">{error}</p>}
      <button className="primary-action" type="submit">Create</button>
    </form>
  )
}
