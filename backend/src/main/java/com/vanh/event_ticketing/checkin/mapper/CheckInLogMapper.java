package com.vanh.event_ticketing.checkin.mapper;

import com.vanh.event_ticketing.checkin.dto.CheckInResponse;
import com.vanh.event_ticketing.checkin.entity.CheckInLog;
import org.springframework.stereotype.Component;

@Component
public class CheckInLogMapper {
    public CheckInResponse toResponse(CheckInLog log) {
        return new CheckInResponse(log.getTicket().getId(), log.getTicket().getStatus(), log.getCheckedInAt(), log.getGate().getId());
    }
}
