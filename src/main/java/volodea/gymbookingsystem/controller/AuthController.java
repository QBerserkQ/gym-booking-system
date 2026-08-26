package volodea.gymbookingsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import volodea.gymbookingsystem.dto.RegisterRequest;
import volodea.gymbookingsystem.dto.RegisterResponse;
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
}
