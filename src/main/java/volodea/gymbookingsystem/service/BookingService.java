package volodea.gymbookingsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import volodea.gymbookingsystem.dto.BookingRequest;
import volodea.gymbookingsystem.dto.BookingResponse;
import volodea.gymbookingsystem.entity.Booking;
import volodea.gymbookingsystem.entity.BookingStatus;
import volodea.gymbookingsystem.entity.GymClass;
import volodea.gymbookingsystem.exception.*;
import volodea.gymbookingsystem.repository.BookingRepository;
import volodea.gymbookingsystem.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final GymClassService gymClassService;
    private final UserRepository userRepository;

    public BookingService(GymClassService gymClassService
            , BookingRepository bookingRepository
            , UserRepository userRepository) {
        this.gymClassService = gymClassService;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest, Long userId) {
        GymClass gymClass = gymClassService.getGymClassById(bookingRequest.gymClassId());

        long confirmed = bookingRepository.countByGymClassIdAndBookingStatus(
                gymClass.getId(), BookingStatus.CONFIRMED
        );
        long pending = bookingRepository.countByGymClassIdAndBookingStatus(
                gymClass.getId(), BookingStatus.PENDING
        );

        if(gymClass.getCapacity() - (confirmed + pending) < 1) {
            throw new NoAvailableSpotsException(gymClass.getId());
        }

        Booking booking = Booking.builder()
                .user(userRepository.findById(userId).orElseThrow(
                        () -> new UserNotFoundException(userId)
                ))
                .gymClass(gymClass)
                .bookingStatus(BookingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        booking =  bookingRepository.save(booking);

        return toResponse(booking, gymClass);
    }

    @Transactional
    public BookingResponse approveBooking(Long bookingId) {
        Booking booking = getPendingBookingOrThrow(bookingId);

        GymClass gymClass = gymClassService.findGymClassByIdForUpdate(booking.getGymClass().getId());

        long confirmed = bookingRepository
                .countByGymClassIdAndBookingStatus(gymClass.getId(), BookingStatus.CONFIRMED);

        if((gymClass.getCapacity() - confirmed) < 1) {
            throw new NoAvailableSpotsException(gymClass.getId());
        }

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);

        return toResponse(saved, gymClass);
    }

    @Transactional
    public BookingResponse rejectBooking(Long bookingId) {
        Booking booking = getPendingBookingOrThrow(bookingId);

        booking.setBookingStatus(BookingStatus.REJECTED);
        Booking saved = bookingRepository.save(booking);

        return toResponse(saved, saved.getGymClass());
    }

    public List<BookingResponse> getPendingBookings() {
        List<Booking> bookings = bookingRepository.findByBookingStatus(BookingStatus.PENDING);

        return bookings.stream().map(
                booking -> toResponse(booking, booking.getGymClass()))
                .toList();
    }

    private Booking getPendingBookingOrThrow(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException(bookingId)
        );

        if (!booking.getBookingStatus().equals(BookingStatus.PENDING)) {
            throw new InvalidBookingStateException(bookingId);
        }

        return booking;
    }

    private BookingResponse toResponse(Booking booking, GymClass gymClass) {
        return new BookingResponse(
                booking.getId(),
                gymClass.getTitle(),
                gymClass.getStartTime(),
                booking.getBookingStatus(),
                booking.getCreatedAt()
        );
    }
}
