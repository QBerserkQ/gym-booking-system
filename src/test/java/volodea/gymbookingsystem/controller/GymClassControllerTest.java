package volodea.gymbookingsystem.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import volodea.gymbookingsystem.config.SecurityConfig;
import volodea.gymbookingsystem.config.jwt.JwtService;
import volodea.gymbookingsystem.dto.GymClassRequest;
import volodea.gymbookingsystem.dto.GymClassResponse;
import volodea.gymbookingsystem.repository.GymRepository;
import volodea.gymbookingsystem.repository.UserRepository;
import volodea.gymbookingsystem.service.GymClassService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GymClassController.class)
@Import(SecurityConfig.class)
public class GymClassControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GymClassService gymClassService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void shouldReturnAllGymClasses() throws Exception {
        List<GymClassResponse> gymClassesList = new ArrayList<>();

        when(gymClassService.getAllGymClasses()).thenReturn(gymClassesList);

        mockMvc.perform(get("/api/gym-classes"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateGymClass() throws Exception {
        GymClassResponse response = new GymClassResponse(1L, "title"
                , LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 30);

        when(gymClassService.createNewGymClass(any(GymClassRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/gym-classes/create")
                        .with(user("vova").roles("SUPPORT"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title" : "title",
                            "capacity" : 30,
                            "startTime" : "2026-12-15T10:00:00"
                        }
                        """)).andExpect(status().isCreated());
    }

    @Test
    void shouldReturn403WhenCreateWithUserRole() throws Exception {
        mockMvc.perform(post("/api/gym-classes/create")
                .with(user("vova").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title" : "title",
                            "capacity" : 30,
                            "startTime" : "2026-12-15T10:00:00"
                        }
                        """)).andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn400WhenJsonContentIsNotValid() throws Exception {
        mockMvc.perform(post("/api/gym-classes/create")
                        .with(user("vova").roles("SUPPORT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "title" : "title",
                            "capacity" : -30,
                            "startTime" : "2026-12-15T10:00:00"
                        }
                        """)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
