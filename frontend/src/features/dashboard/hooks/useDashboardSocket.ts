import { useEffect, useState } from 'react'
import { createStompClient } from '../../../lib/stompClient'

export function useDashboardSocket(eventId: number | null, accessToken: string | null, onMessage: () => void) {
  const [connected, setConnected] = useState(false)

  useEffect(() => {
    if (!eventId) {
      return
    }

    const client = createStompClient(accessToken)
    client.onConnect = () => {
      setConnected(true)
      client.subscribe(`/topic/dashboard/${eventId}`, onMessage)
    }
    client.onDisconnect = () => setConnected(false)
    client.onStompError = () => setConnected(false)
    client.activate()

    return () => {
      setConnected(false)
      void client.deactivate()
    }
  }, [accessToken, eventId, onMessage])

  return connected
}
