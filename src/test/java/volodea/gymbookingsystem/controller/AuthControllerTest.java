package volodea.gymbookingsystem.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import volodea.gymbookingsystem.config.SecurityConfig;
import volodea.gymbookingsystem.config.jwt.JwtService;
import volodea.gymbookingsystem.dto.*;
import volodea.gymbookingsystem.exception.EmailAlreadyExistsException;
import volodea.gymbookingsystem.exception.InvalidCredentialsException;
import volodea.gymbookingsystem.repository.UserRepository;
import volodea.gymbookingsystem.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void shouldRegisterUser() throws Exception {
        RegisterResponse registerResponse = new RegisterResponse("username", "email");

        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(registerResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "username",
                            "email": "email@mail.ru",
                            "password": "password"
                        }
                        """))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        when(authService.registerUser(any(RegisterRequest.class))).thenThrow(new EmailAlreadyExistsException("email@mail.ru"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "username",
                            "email": "email@mail.ru",
                            "password": "password"
                        }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void shouldReturn400WhenInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "username",
                            "email": "email.ru",
                            "password": "password"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldLoginUser() throws Exception {
        LoginResponse loginResponse = new LoginResponse("jwtToken", "refToken");

        when(authService.loginUser(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "email": "email@mail.ru",
                            "password": "password"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").value("jwtToken"))
                .andExpect(jsonPath("$.refreshToken").value("refToken"));
    }

    @Test
    void shouldReturn401WhenCredentialsAreInvalid() throws Exception {
        when(authService.loginUser(any(LoginRequest.class))).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "email": "email@mail.ru",
                            "password": "wrongPassword"
                        }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void shouldRefreshToken() throws Exception {
        LoginResponse loginResponse = new LoginResponse("jwtToken", "refToken");

        when(authService.refreshToken(any(RefreshRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "refreshToken" : "refreshToken"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").value("jwtToken"))
                .andExpect(jsonPath("$.refreshToken").value("refToken"));
    }

    @Test
    void shouldLogoutUser() throws Exception {
        Long userId = 1L;

        mockMvc.perform(post("/api/auth/logout")
                .with(user("1").roles("USER")))
                .andExpect(status().isNoContent());

        verify(authService).logoutUser(userId);
    }

    @Test
    void shouldReturn401WhenLogoutWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isForbidden());
    }
}
