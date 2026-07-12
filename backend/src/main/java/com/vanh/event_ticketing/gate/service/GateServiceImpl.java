package com.vanh.event_ticketing.gate.service;

import com.vanh.event_ticketing.common.exception.BusinessException;
import com.vanh.event_ticketing.common.exception.ErrorCode;
import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.event.entity.Event;
import com.vanh.event_ticketing.event.repository.EventRepository;
import com.vanh.event_ticketing.gate.dto.GateRequest;
import com.vanh.event_ticketing.gate.dto.GateResponse;
import com.vanh.event_ticketing.gate.entity.Gate;
import com.vanh.event_ticketing.gate.repository.GateRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GateServiceImpl implements GateService {
    private final EventRepository eventRepository;
    private final GateRepository gateRepository;

    @Override
    @Transactional
    public GateResponse create(Long eventId, GateRequest request, CustomUserDetails userDetails) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));
        requireOwner(event, userDetails);
        Gate gate = new Gate();
        gate.setEvent(event);
        gate.setName(request.name().trim());
        return toResponse(gateRepository.save(gate));
    }

    @Override
    public List<GateResponse> list(Long eventId, CustomUserDetails userDetails) {
        return gateRepository.findByEventId(eventId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public GateResponse update(Long id, GateRequest request, CustomUserDetails userDetails) {
        Gate gate = gateRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.GATE_NOT_FOUND));
        requireOwner(gate.getEvent(), userDetails);
        gate.setName(request.name().trim());
        return toResponse(gate);
    }

    @Override
    @Transactional
    public void delete(Long id, CustomUserDetails userDetails) {
        Gate gate = gateRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.GATE_NOT_FOUND));
        requireOwner(gate.getEvent(), userDetails);
        gateRepository.delete(gate);
    }

    private void requireOwner(Event event, CustomUserDetails userDetails) {
        if (!event.getOrganizer().getId().equals(userDetails.getId())) {
            throw new BusinessException(ErrorCode.EVENT_OWNERSHIP_VIOLATION);
        }
    }

    private GateResponse toResponse(Gate gate) {
        return new GateResponse(gate.getId(), gate.getEvent().getId(), gate.getName());
    }
}
