package volodea.gymbookingsystem.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import volodea.gymbookingsystem.dto.UserRequest;
import volodea.gymbookingsystem.dto.UserResponse;
import volodea.gymbookingsystem.service.UserService;

@Tag(name = "Users", description = "Endpoints for managing user roles (Only Admin).")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/{userId}/role")
    public UserResponse updateRole(
            @PathVariable Long userId, @Valid @RequestBody UserRequest userRequest) {
        return userService.updateUserRole(userId, userRequest);
    }
}
