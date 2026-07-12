import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

export function createStompClient(accessToken: string | null) {
  const wsUrl = (import.meta.env.VITE_WS_URL ?? 'http://localhost:8080/ws')
    .replace(/^ws:/, 'http:')
    .replace(/^wss:/, 'https:')

  return new Client({
    connectHeaders: accessToken ? { Authorization: `Bearer ${accessToken}` } : {},
    reconnectDelay: 3000,
    webSocketFactory: () => new SockJS(wsUrl),
  })
}
