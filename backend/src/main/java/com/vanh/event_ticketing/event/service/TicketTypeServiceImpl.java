package com.vanh.event_ticketing.event.service;

import com.vanh.event_ticketing.common.exception.BusinessException;
import com.vanh.event_ticketing.common.exception.ErrorCode;
import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.event.dto.TicketTypeRequest;
import com.vanh.event_ticketing.event.dto.TicketTypeResponse;
import com.vanh.event_ticketing.event.entity.Event;
import com.vanh.event_ticketing.event.entity.TicketType;
import com.vanh.event_ticketing.event.mapper.TicketTypeMapper;
import com.vanh.event_ticketing.event.repository.EventRepository;
import com.vanh.event_ticketing.event.repository.TicketTypeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {
    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketTypeMapper ticketTypeMapper;

    @Override
    @Transactional
    public TicketTypeResponse create(Long eventId, TicketTypeRequest request, CustomUserDetails userDetails) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));
        requireOwner(event, userDetails);
        TicketType ticketType = new TicketType();
        ticketType.setEvent(event);
        apply(ticketType, request, true);
        return ticketTypeMapper.toResponse(ticketTypeRepository.save(ticketType));
    }

    @Override
    public List<TicketTypeResponse> list(Long eventId) {
        return ticketTypeRepository.findByEventIdAndDeletedAtIsNull(eventId).stream().map(ticketTypeMapper::toResponse).toList();
    }

    @Override
    public TicketTypeResponse get(Long id) {
        return ticketTypeMapper.toResponse(findActive(id));
    }

    @Override
    @Transactional
    public TicketTypeResponse update(Long id, TicketTypeRequest request, CustomUserDetails userDetails) {
        TicketType ticketType = findActive(id);
        requireOwner(ticketType.getEvent(), userDetails);
        apply(ticketType, request, false);
        return ticketTypeMapper.toResponse(ticketType);
    }

    private TicketType findActive(Long id) {
        TicketType ticketType = ticketTypeRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.TICKET_TYPE_NOT_FOUND));
        if (ticketType.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.TICKET_TYPE_NOT_FOUND);
        }
        return ticketType;
    }

    private void requireOwner(Event event, CustomUserDetails userDetails) {
        if (!event.getOrganizer().getId().equals(userDetails.getId())) {
            throw new BusinessException(ErrorCode.EVENT_OWNERSHIP_VIOLATION);
        }
    }

    private void apply(TicketType ticketType, TicketTypeRequest request, boolean creating) {
        ticketType.setName(request.name().trim());
        ticketType.setPrice(request.price());
        if (creating) {
            ticketType.setQuantityTotal(request.quantityTotal());
            ticketType.setQuantityRemaining(request.quantityTotal());
        }
        ticketType.setSalesStartAt(request.salesStartAt());
        ticketType.setSalesEndAt(request.salesEndAt());
    }
}
