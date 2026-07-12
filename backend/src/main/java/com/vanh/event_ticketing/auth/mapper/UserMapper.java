package com.vanh.event_ticketing.auth.mapper;

import com.vanh.event_ticketing.auth.dto.UserResponse;
import com.vanh.event_ticketing.auth.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().getName(),
                user.getFullName(),
                user.getAssignedEventId(),
                user.isActive()
        );
    }
}
