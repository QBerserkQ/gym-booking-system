package volodea.gymbookingsystem.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import volodea.gymbookingsystem.entity.GymClass;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GymRepositoryTest {

    @Autowired
    private GymRepository gymRepository;

    @Test
    void shouldFindAllGyms() {
        GymClass gymClass = GymClass.builder()
                .title("Main room")
                .startTime(LocalDateTime.now())
                .build();

        gymRepository.save(gymClass);

        List<GymClass> gymList = gymRepository.findAll();

        assertThat(gymList).hasSize(1);
        assertThat(gymList.get(0).getTitle()).isEqualTo("Main room");
    }

    @Test
    void shouldFindGymById() {
        GymClass gymClass = GymClass.builder()
                .title("Main room")
                .startTime(LocalDateTime.now())
                .build();

        GymClass saved = gymRepository.save(gymClass);

        Optional<GymClass> gym = gymRepository.findById(saved.getId());

        assertThat(gym).isPresent();
        assertThat(gym.get().getId()).isNotNull();
        assertThat(gym.get().getTitle()).isEqualTo("Main room");
    }

    @Test
    void shouldNotFindGymByUnknowId() {
        Optional<GymClass> gym = gymRepository.findById(1000000L);

        assertThat(gym).isEmpty();
    }
}
