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
import volodea.gymbookingsystem.dto.UserRequest;
import volodea.gymbookingsystem.dto.UserResponse;
import volodea.gymbookingsystem.entity.Role;
import volodea.gymbookingsystem.exception.UserNotFoundException;
import volodea.gymbookingsystem.repository.UserRepository;
import volodea.gymbookingsystem.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void shouldChangeRole() throws Exception {
        UserResponse response = new UserResponse(1L, Role.SUPPORT);

        when(userService.updateUserRole(any(Long.class), any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/users/1/role")
                        .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "role" : "SUPPORT"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("SUPPORT"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void shouldReturn403WhenChangeRoleWithoutAdminRole() throws Exception {
        mockMvc.perform(patch("/api/users/1/role")
                        .with(user("support").roles("SUPPORT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "role" : "SUPPORT"
                        }
                        """))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(userService.updateUserRole(any(Long.class), any(UserRequest.class)))
                .thenThrow(new UserNotFoundException(1L));

        mockMvc.perform(patch("/api/users/1/role")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "role" : "SUPPORT"
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
