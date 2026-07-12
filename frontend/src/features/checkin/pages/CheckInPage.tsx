import { useEffect, useState } from 'react'
import { QrCode } from 'lucide-react'
import { listEvents, listGates } from '../../events/api/eventApi'
import type { EventItem, GateItem } from '../../events/types'
import { checkIn } from '../api/checkinApi'
import { QrScanner } from '../components/QrScanner'
import type { CheckInResult } from '../types'

export function CheckInPage() {
  const [events, setEvents] = useState<EventItem[]>([])
  const [eventId, setEventId] = useState<number | null>(null)
  const [gates, setGates] = useState<GateItem[]>([])
  const [gateId, setGateId] = useState<number | null>(null)
  const [qrCode, setQrCode] = useState('')
  const [result, setResult] = useState<CheckInResult | null>(null)
  const [error, setError] = useState('')

  useEffect(() => {
    listEvents().then((items) => {
      setEvents(items)
      setEventId(items[0]?.id ?? null)
    })
  }, [])

  useEffect(() => {
    if (!eventId) return
    listGates(eventId).then((items) => {
      setGates(items)
      setGateId(items[0]?.id ?? null)
    })
  }, [eventId])

  async function submit(code = qrCode) {
    if (!gateId || !code.trim()) return
    setError('')
    setResult(null)
    try {
      setResult(await checkIn(code.trim(), gateId))
      setQrCode('')
    } catch {
      setError('Check-in failed. Ticket may be invalid, duplicate, or outside the allowed window.')
    }
  }

  return (
    <section className="checkin-page">
      <header className="checkin-header">
        <select value={eventId ?? ''} onChange={(event) => setEventId(Number(event.target.value))}>
          {events.map((event) => <option key={event.id} value={event.id}>{event.name}</option>)}
        </select>
        <select value={gateId ?? ''} onChange={(event) => setGateId(Number(event.target.value))}>
          {gates.map((gate) => <option key={gate.id} value={gate.id}>{gate.name}</option>)}
        </select>
      </header>
      <QrScanner onScan={(code) => void submit(code)} />
      <form className="manual-checkin" onSubmit={(event) => {
        event.preventDefault()
        void submit()
      }}>
        <label>
          QR code
          <input value={qrCode} onChange={(event) => setQrCode(event.target.value)} />
        </label>
        <button className="primary-action" type="submit">
          <QrCode size={18} />
          Check in
        </button>
      </form>
      {result && (
        <section className="checkin-result success">
          <strong>Check-in successful</strong>
          <span>Ticket #{result.ticketId} · Gate #{result.gateId} · {new Date(result.checkedInAt).toLocaleTimeString()}</span>
        </section>
      )}
      {error && (
        <section className="checkin-result error">
          <strong>Check-in failed</strong>
          <span>{error}</span>
        </section>
      )}
    </section>
  )
}
