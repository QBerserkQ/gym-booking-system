package volodea.gymbookingsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import volodea.gymbookingsystem.dto.*;
import volodea.gymbookingsystem.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest){
         return ResponseEntity
                 .status(HttpStatus.CREATED)
                 .body(authService.registerUser(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity
                 .status(HttpStatus.OK)
                 .body(authService.loginUser(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest refreshRequest){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.refreshToken(refreshRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication){
        Long userId = Long.parseLong(authentication.getName());

        authService.logoutUser(userId);

        return ResponseEntity.noContent().build();
    }
}
