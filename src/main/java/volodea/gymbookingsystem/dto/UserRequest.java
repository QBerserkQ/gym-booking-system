package volodea.gymbookingsystem.dto;

import jakarta.validation.constraints.NotNull;
import volodea.gymbookingsystem.entity.Role;

public record UserRequest(
        @NotNull Role role
) {
}
