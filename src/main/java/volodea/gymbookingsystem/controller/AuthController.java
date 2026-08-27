package volodea.gymbookingsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import volodea.gymbookingsystem.config.jwt.JwtService;
import volodea.gymbookingsystem.dto.LoginRequest;
import volodea.gymbookingsystem.dto.LoginResponse;
import volodea.gymbookingsystem.dto.RegisterRequest;
import volodea.gymbookingsystem.dto.RegisterResponse;
import volodea.gymbookingsystem.entity.User;
import volodea.gymbookingsystem.exception.InvalidCredentialsException;
import volodea.gymbookingsystem.repository.UserRepository;
import volodea.gymbookingsystem.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest){
         return ResponseEntity
                 .status(HttpStatus.CREATED)
                 .body(authService.registerUser(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(
                InvalidCredentialsException::new
        );

        if(!passwordEncoder.matches(loginRequest.password(), user.getPasswordHashed())){
            throw new InvalidCredentialsException();
        }

        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(jwtService.generateToken(user)));
    }
}
