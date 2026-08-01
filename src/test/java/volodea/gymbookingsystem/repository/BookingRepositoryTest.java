package volodea.gymbookingsystem.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import volodea.gymbookingsystem.entity.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GymRepository gymRepository;

    private User user;
    private GymClass gymClass;

    @BeforeEach
    public void setup() {
        user =  User.builder()
                .username("Vova")
                .passwordHashed("adada")
                .email("vk@gmail.com")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        gymClass = GymClass.builder()
                .title("Main room")
                .startTime(LocalDateTime.now())
                .build();
        gymRepository.save(gymClass);
    }

    @Test
    void findBookingByIdTest(){
        Booking booking = Booking.builder()
                .user(user)
                .gymClass(gymClass)
                .bookingStatus(BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();

        Booking saved = bookingRepository.save(booking);

        Optional<Booking> returned = bookingRepository.findById(saved.getId());

        assertThat(returned).isPresent();
        assertThat(returned.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    void shouldReturnCountBookingByIdAndStatus(){
        bookingRepository.saveAll(List.of(
                createBooking(user, gymClass, BookingStatus.CONFIRMED)
                , createBooking(user, gymClass, BookingStatus.CONFIRMED)
                , createBooking(user, gymClass, BookingStatus.CONFIRMED)
                , createBooking(user, gymClass, BookingStatus.REJECTED)
                , createBooking(user, gymClass, BookingStatus.PENDING)
        ));

        long confirmed = bookingRepository.countByGymClassIdAndBookingStatus(gymClass.getId(), BookingStatus.CONFIRMED);
        long rejected = bookingRepository.countByGymClassIdAndBookingStatus(gymClass.getId(), BookingStatus.REJECTED);
        long pending = bookingRepository.countByGymClassIdAndBookingStatus(gymClass.getId(), BookingStatus.PENDING);

        assertThat(confirmed).isEqualTo(3);
        assertThat(rejected).isEqualTo(1);
        assertThat(pending).isEqualTo(1);
    }

    private Booking createBooking(User user, GymClass gymClass, BookingStatus bookingStatus){
        return Booking.builder()
                .user(user)
                .gymClass(gymClass)
                .bookingStatus(bookingStatus)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
