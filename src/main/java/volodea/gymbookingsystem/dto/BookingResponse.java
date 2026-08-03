package volodea.gymbookingsystem.dto;

import volodea.gymbookingsystem.entity.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        String gymClassTitle,
        LocalDateTime startTime,
        BookingStatus bookingStatus,
        LocalDateTime createdAt
) {
}

