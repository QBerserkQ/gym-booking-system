package volodea.gymbookingsystem.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import volodea.gymbookingsystem.dto.GymClassRequest;
import volodea.gymbookingsystem.dto.GymClassResponse;
import volodea.gymbookingsystem.entity.GymClass;
import volodea.gymbookingsystem.repository.GymRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GymClassServiceTest {
    @InjectMocks
    private GymClassService gymClassService;

    @Mock
    private GymRepository gymRepository;

    @Test
    void shouldCreateNewGymClass() {
        GymClassRequest gymClassRequest = new GymClassRequest("Warzone"
                , 30, LocalDateTime.now().plusDays(1));

        GymClass saved = GymClass.builder()
                .id(1L)
                .title("Warzone")
                .capacity(30)
                .startTime(LocalDateTime.now().plusDays(1)).build();

        when(gymRepository.save(any(GymClass.class))).thenReturn(saved);

        GymClassResponse response = gymClassService.createNewGymClass(gymClassRequest);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.endDate()).isEqualTo(saved.getStartTime().plusHours(2));
    }
}
