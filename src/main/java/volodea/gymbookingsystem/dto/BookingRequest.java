package volodea.gymbookingsystem.dto;

import jakarta.validation.constraints.NotNull;

public record BookingRequest(
    @NotNull Long gymClassId
) { }
