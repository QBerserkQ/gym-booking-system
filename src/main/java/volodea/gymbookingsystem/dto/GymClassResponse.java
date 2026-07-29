package volodea.gymbookingsystem.dto;

import java.time.LocalDateTime;

public record GymClassResponse(
    Long id,
    String title,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Integer freeSpots
) { }
