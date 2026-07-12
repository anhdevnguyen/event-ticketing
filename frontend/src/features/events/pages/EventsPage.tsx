import { useEffect, useState } from 'react'
import { PageTitle } from '../../../components/layout/PageTitle'
import { navigate } from '../../../routes/navigation'
import { listEvents } from '../api/eventApi'
import type { EventItem } from '../types'

export function EventsPage() {
  const [events, setEvents] = useState<EventItem[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    listEvents()
      .then(setEvents)
      .catch(() => setError('Could not load events. Check backend and PostgreSQL are running.'))
      .finally(() => setLoading(false))
  }, [])

  return (
    <section className="page">
      <PageTitle eyebrow="Customer booking" title="Events" action={<span>{events.length} listed</span>} />
      {loading && <p className="page-message">Loading events...</p>}
      {error && <p className="form-error">{error}</p>}
      {!loading && !error && events.length === 0 && <p className="page-message">No events found. Check Flyway seed data.</p>}
      <div className="event-grid">
        {events.map((event) => (
          <article className="event-card" key={event.id}>
            {event.bannerUrl ? <img alt={`${event.name} banner`} src={event.bannerUrl} /> : <div className="banner-fallback" />}
            <div>
              <strong>{event.status}</strong>
              <h2>{event.name}</h2>
              <p>{event.description ?? 'No description yet.'}</p>
              <span>
                {event.location} · {new Date(event.startTime).toLocaleString()}
              </span>
              <button className="primary-action" type="button" onClick={() => navigate(`/events/${event.id}`)}>
                View tickets
              </button>
            </div>
          </article>
        ))}
      </div>
    </section>
  )
}
