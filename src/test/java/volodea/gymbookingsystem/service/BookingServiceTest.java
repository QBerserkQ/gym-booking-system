package volodea.gymbookingsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import volodea.gymbookingsystem.dto.BookingRequest;
import volodea.gymbookingsystem.dto.BookingResponse;
import volodea.gymbookingsystem.entity.Booking;
import volodea.gymbookingsystem.entity.BookingStatus;
import volodea.gymbookingsystem.entity.GymClass;
import volodea.gymbookingsystem.entity.User;
import volodea.gymbookingsystem.exception.*;
import volodea.gymbookingsystem.repository.BookingRepository;
import volodea.gymbookingsystem.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private GymClassService gymClassService;
    @Mock
    private UserRepository userRepository;

    @Test
    void shouldCreateBookingWhenSpotsAvailable() {
        GymClass gymClass = GymClass.builder()
                .id(1L).title("Main Gym").capacity(30).build();

        User user = User.builder()
                .id(1L).username("Vova").build();

        when(gymClassService.getGymClassById(1L)).thenReturn(gymClass);
        when(bookingRepository.countByGymClassIdAndBookingStatus(1L, BookingStatus.CONFIRMED))
                .thenReturn(10L);
        when(bookingRepository.countByGymClassIdAndBookingStatus(1L, BookingStatus.PENDING))
                .thenReturn(5L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponse response = bookingService
                .createBooking(new BookingRequest(1L), 1L);

        assertThat(response.bookingStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(response.gymClassTitle()).isEqualTo("Main Gym");
    }

    @Test
    void shouldThrowWhenNoSpotsAvailable() {
        GymClass gymClass = GymClass.builder()
                .id(1L).title("Main Gym").capacity(15).build();

        when(gymClassService.getGymClassById(1L)).thenReturn(gymClass);
        when(bookingRepository.countByGymClassIdAndBookingStatus(1L, BookingStatus.CONFIRMED))
                .thenReturn(10L);
        when(bookingRepository.countByGymClassIdAndBookingStatus(1L, BookingStatus.PENDING))
                .thenReturn(5L);

        assertThrows(NoAvailableSpotsException.class
                , () -> bookingService.createBooking(new BookingRequest(1L), 1L));
    }

    @Test
    void shouldThrowWhenInvalidUserId() {
        GymClass gymClass = GymClass.builder()
                .id(1L).title("Main Gym").capacity(30).build();

        when(gymClassService.getGymClassById(1L)).thenReturn(gymClass);
        when(bookingRepository.countByGymClassIdAndBookingStatus(1L, BookingStatus.CONFIRMED))
                .thenReturn(10L);
        when(bookingRepository.countByGymClassIdAndBookingStatus(1L, BookingStatus.PENDING))
                .thenReturn(5L);

        assertThrows(UserNotFoundException.class
                , () -> bookingService.createBooking(new BookingRequest(1L), 1L));
    }

    @Test
    void shouldThrowWhenGymClassNotFound() {
        when(gymClassService.getGymClassById(99L))
                .thenThrow(new GymClassNotFoundException(99L));

        assertThrows(GymClassNotFoundException.class, () ->
                bookingService.createBooking(new BookingRequest(99L), 1L));
    }

    @Test
    void shouldApproveBooking(){
        GymClass gymClass = GymClass.builder()
                .id(1L).title("Main Gym").capacity(30).build();

        Booking booking = Booking.builder()
                .id(1L).bookingStatus(BookingStatus.PENDING).gymClass(gymClass).build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(gymClassService.findGymClassByIdForUpdate(1L)).thenReturn(gymClass);
        when(bookingRepository.countByGymClassIdAndBookingStatus(1L, BookingStatus.CONFIRMED))
                .thenReturn(10L);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));


        BookingResponse response = bookingService.approveBooking(1L);

        assertThat(response.bookingStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(response.gymClassTitle()).isEqualTo("Main Gym");
    }

    @Test
    void shouldRejectBooking(){
        GymClass gymClass = GymClass.builder()
                .id(1L).title("Main Gym").capacity(30).build();

        Booking booking = Booking.builder()
                .id(1L).bookingStatus(BookingStatus.PENDING).gymClass(gymClass).build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));


        BookingResponse response = bookingService.rejectBooking(1L);

        assertThat(response.bookingStatus()).isEqualTo(BookingStatus.REJECTED);
        assertThat(response.gymClassTitle()).isEqualTo("Main Gym");
    }


    @Test
    void shouldThrowWhenApprovingNonPendingBooking(){
        Booking booking = Booking.builder()
                .id(1L).bookingStatus(BookingStatus.REJECTED).gymClass(GymClass.builder().build()).build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(InvalidBookingStateException.class,
                () ->  bookingService.approveBooking(1L));
    }

    @Test
    void shouldThrowWhenApprovingWithNoAvailableSpots(){
        GymClass gymClass = GymClass.builder()
                .id(1L).title("Main Gym").capacity(10).build();

        Booking booking = Booking.builder()
                .id(1L).bookingStatus(BookingStatus.PENDING).gymClass(gymClass).build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(gymClassService.findGymClassByIdForUpdate(1L)).thenReturn(gymClass);
        when(bookingRepository.countByGymClassIdAndBookingStatus(1L, BookingStatus.CONFIRMED))
                .thenReturn(10L);


        assertThrows(NoAvailableSpotsException.class,
                () -> bookingService.approveBooking(1L));
    }

    @Test
    void shouldThrowWhenBookingNotFound(){
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.approveBooking(99L));
    }
}
