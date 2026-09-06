package volodea.gymbookingsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import volodea.gymbookingsystem.config.jwt.JwtService;
import volodea.gymbookingsystem.dto.*;
import volodea.gymbookingsystem.entity.RefreshToken;
import volodea.gymbookingsystem.entity.Role;
import volodea.gymbookingsystem.entity.User;
import volodea.gymbookingsystem.exception.EmailAlreadyExistsException;
import volodea.gymbookingsystem.exception.InvalidCredentialsException;
import volodea.gymbookingsystem.exception.RefreshTokenNotFoundException;
import volodea.gymbookingsystem.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    AuthService authService;

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtService jwtService;
    @Mock
    RefreshTokenService refreshTokenService;

    @Test
    void shouldRegisterUser(){
        RegisterRequest registerRequest = new RegisterRequest("username", "email", "password");
        User user = User.builder()
                .id(1L).role(Role.USER)
                .email("email").passwordHashed("password")
                .username("username").build();

        when(userRepository.existsByEmail("email")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        RegisterResponse registerResponse = authService.registerUser(registerRequest);

        assertThat(registerResponse).isNotNull();
        assertThat(registerResponse.username()).isEqualTo("username");
        assertThat(registerResponse.email()).isEqualTo("email");
    }

    @Test
    void shouldThrowExceptionWhenEmailIsAlreadyUsed() {
        RegisterRequest registerRequest = new RegisterRequest("username", "email", "password");

        when(userRepository.existsByEmail("email")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.registerUser(registerRequest));
    }

    @Test
    void shouldLoginUser(){
        LoginRequest loginRequest = new LoginRequest("email", "password");

        User user = User.builder()
                .id(1L).role(Role.USER)
                .email("email").passwordHashed("hashedPassword")
                .username("username").build();

        RefreshToken refreshToken = RefreshToken.builder().token("token").build();

        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", user.getPasswordHashed())).thenReturn(true);
        when(refreshTokenService.generateRefreshToken(user)).thenReturn(refreshToken);
        when(jwtService.generateJwtToken(user)).thenReturn("jwtToken");

        LoginResponse loginResponse = authService.loginUser(loginRequest);

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.jwtToken()).isEqualTo("jwtToken");
        assertThat(loginResponse.refreshToken()).isEqualTo("token");
    }

    @Test
    void shouldThrowExceptionWhenEmailNotFound() {
        LoginRequest loginRequest = new LoginRequest("email", "password");
        when(userRepository.findByEmail("email")).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(loginRequest));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIncorrect() {
        LoginRequest loginRequest = new LoginRequest("email", "incorrectPassword");

        User user = User.builder().passwordHashed("correctPassword").username("username").build();

        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("incorrectPassword"
                , "correctPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(loginRequest));
    }

    @Test
    void shouldRefreshToken(){
        RefreshRequest refreshRequest = new RefreshRequest("oldToken");

        User user = User.builder().id(1L).build();
        RefreshToken verifiedRefreshToken = RefreshToken.builder().token("oldToken").user(user).build();
        RefreshToken newRefreshToken = RefreshToken.builder().token("newToken").build();


        when(refreshTokenService.verifyRefreshToken("oldToken")).thenReturn(verifiedRefreshToken);
        when(refreshTokenService.generateRefreshToken(user)).thenReturn(newRefreshToken);
        when(jwtService.generateJwtToken(user)).thenReturn("jwtToken");

        LoginResponse loginResponse = authService.refreshToken(refreshRequest);
        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.jwtToken()).isEqualTo("jwtToken");
        assertThat(loginResponse.refreshToken()).isEqualTo("newToken");
    }

    @Test
    void shouldThrowExceptionWhenTokenInvalid() {
        RefreshRequest refreshRequest = new RefreshRequest("badToken");

        when(refreshTokenService.verifyRefreshToken("badToken")).thenThrow(new RefreshTokenNotFoundException("badToken"));
        assertThrows(RefreshTokenNotFoundException.class, () -> authService.refreshToken(refreshRequest));
    }

    @Test
    void shouldLogoutUser(){
        Long userId = 1L;
        User user = User.builder().id(userId).build();

        when(userRepository.getReferenceById(userId)).thenReturn(user);

        authService.logoutUser(userId);

        verify(refreshTokenService).deleteRefreshTokenByUser(user);
    }
}
