package com.vanh.event_ticketing.dashboard.websocket;

import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardEventPublisher {
    private final SimpMessagingTemplate messagingTemplate;

    public void publish(Long eventId, Long gateId) {
        Map<String, Object> message = Map.of("eventId", eventId, "gateId", gateId, "timestamp", Instant.now().toString());
        messagingTemplate.convertAndSend("/topic/dashboard/" + eventId, message);
        messagingTemplate.convertAndSend("/topic/dashboard/" + eventId + "/" + gateId, message);
    }
}
