package volodea.gymbookingsystem.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import volodea.gymbookingsystem.config.jwt.JwtService;
import volodea.gymbookingsystem.dto.*;
import volodea.gymbookingsystem.entity.RefreshToken;
import volodea.gymbookingsystem.entity.Role;
import volodea.gymbookingsystem.entity.User;
import volodea.gymbookingsystem.exception.EmailAlreadyExistsException;
import volodea.gymbookingsystem.exception.InvalidCredentialsException;
import volodea.gymbookingsystem.exception.UserNotFoundException;
import volodea.gymbookingsystem.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public RegisterResponse registerUser(RegisterRequest registerRequest) {

        if(userRepository.existsByEmail(registerRequest.email())){
            throw new EmailAlreadyExistsException(registerRequest.email());
        }

        User user = User.builder()
                .username(registerRequest.username())
                .email(registerRequest.email())
                .passwordHashed(passwordEncoder.encode(registerRequest.password()))
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        return new RegisterResponse(user.getUsername(), user.getEmail());
    }

    public LoginResponse loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(
                InvalidCredentialsException::new
        );

        if(!passwordEncoder.matches(loginRequest.password(), user.getPasswordHashed())){
            throw new InvalidCredentialsException();
        }

        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user);

        String refToken = refreshToken.getToken();

        return new LoginResponse(jwtService.generateJwtToken(user), refToken);
    }

    public LoginResponse refreshToken(RefreshRequest refreshRequest) {
        RefreshToken verifiedToken = refreshTokenService.verifyRefreshToken(refreshRequest.refreshToken());

        User user = verifiedToken.getUser();

        RefreshToken newRefreshToken = refreshTokenService.generateRefreshToken(user);

        String accessToken = jwtService.generateJwtToken(user);

        return new LoginResponse(accessToken, newRefreshToken.getToken());
    }

    public void logoutUser(Long userId) {
        User user = userRepository.getReferenceById(userId);

        refreshTokenService.deleteRefreshTokenByUser(user);
    }
}
