package volodea.gymbookingsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import volodea.gymbookingsystem.dto.UserRequest;
import volodea.gymbookingsystem.dto.UserResponse;
import volodea.gymbookingsystem.service.UserService;

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
