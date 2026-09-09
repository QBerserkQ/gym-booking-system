package volodea.gymbookingsystem.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record GymClassRequest(
       @NotBlank String title
        , @NotNull @Positive Integer capacity
        , @NotNull @Future LocalDateTime startTime
) {
}
