// Package: com.vanh.event_ticketing.common.config
// File: WebSocketConfig.java
//
// Vai trò: Cấu hình WebSocket + STOMP message broker cho real-time dashboard.
// Annotate @Configuration, @EnableWebSocketMessageBroker
// Implements WebSocketMessageBrokerConfigurer
//
// === CONFIGURATION ===
//
// configureMessageBroker(MessageBrokerRegistry registry)
//   - registry.enableSimpleBroker("/topic")
//     -> Simple in-memory broker cho /topic prefix
//     -> Production: thay bằng RabbitMQ/ActiveMQ broker (enableStompBrokerRelay)
//   - registry.setApplicationDestinationPrefixes("/app")
//     -> Client gửi message tới server dùng prefix /app
//     -> Ví dụ: /app/dashboard -> @MessageMapping("/dashboard")
//
// registerStompEndpoints(StompEndpointRegistry registry)
//   - registry.addEndpoint("/ws")
//       .setAllowedOriginPatterns("*")  -> config từ env FRONTEND_URL
//       .withSockJS()
//     -> SockJS fallback cho browser không hỗ trợ WebSocket native
//
// === CLIENT USAGE EXAMPLE ===
// Connect: const socket = new SockJS('/ws');
//          const stompClient = Stomp.over(socket);
//          stompClient.connect({}, () => {
//              stompClient.subscribe('/topic/dashboard/123', (msg) => {
//                  const snapshot = JSON.parse(msg.body);
//                  // update UI
//              });
//          });
//
// === GHI CHÚ KỸ THUẬT ===
// - Authentication qua WebSocket: truyền JWT trong STOMP CONNECT headers
//   -> Cần ChannelInterceptor để validate JWT khi CONNECT
// - setAllowedOriginPatterns: đọc từ env FRONTEND_URL, không dùng "*" trong production
// - SimpleBroker không support external broker — scale theo horizontal cần upgrade
// - Heartbeat: registry.enableSimpleBroker("/topic").setHeartbeatValue(new long[]{10000, 10000})
