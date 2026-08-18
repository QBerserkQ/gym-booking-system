package volodea.gymbookingsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import volodea.gymbookingsystem.entity.*;
import volodea.gymbookingsystem.exception.NoAvailableSpotsException;
import volodea.gymbookingsystem.repository.BookingRepository;
import volodea.gymbookingsystem.repository.GymRepository;
import volodea.gymbookingsystem.repository.UserRepository;
import volodea.gymbookingsystem.service.BookingService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class BookingRaceConditionTC {
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configurationProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingService bookingService;

    @Test
    void onlyOneApprovalShouldSucceedForLastSpot() throws InterruptedException {
        User user = userRepository.save(User.builder()
                .username("vova").passwordHashed("123")
                .email("@gmail.com").role(Role.USER).build());

        GymClass gymClass = gymRepository.save(GymClass.builder()
                .capacity(1).title("Gym 1").startTime(LocalDateTime.now())
                .build());

        Booking booking1 = bookingRepository.save(Booking.builder()
                .user(user).gymClass(gymClass).bookingStatus(BookingStatus.PENDING).createdAt(LocalDateTime.now())
                .build());

        Booking booking2 = bookingRepository.save(Booking.builder()
                .user(user).gymClass(gymClass).bookingStatus(BookingStatus.PENDING).createdAt(LocalDateTime.now())
                .build());

        ExecutorService  executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        List<Exception> errors = Collections.synchronizedList(new ArrayList<>());

        Runnable task1 = () -> {
            try{
                bookingService.approveBooking(booking1.getId());
            }catch (Exception e){
                errors.add(e);
            } finally {
                latch.countDown();
            }
        };

        Runnable task2 = () -> {
            try{
                bookingService.approveBooking(booking2.getId());
            }catch (Exception e){
                errors.add(e);
            } finally {
                latch.countDown();
            }
        };

        executor.execute(task1);
        executor.execute(task2);
        latch.await();

        assertThat(errors).hasSize(1);
        assertThat(errors.get(0)).isInstanceOf(NoAvailableSpotsException.class);

        long confirmedBookings = bookingRepository.countByGymClassIdAndBookingStatus(gymClass.getId(), BookingStatus.CONFIRMED);
        assertThat(confirmedBookings).isEqualTo(1);
    }
}
