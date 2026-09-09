package volodea.gymbookingsystem.dto;

import volodea.gymbookingsystem.entity.Role;

public record UserResponse(
        Long userId
        , Role role
) {
}
