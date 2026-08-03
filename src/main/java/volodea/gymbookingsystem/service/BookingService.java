package volodea.gymbookingsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import volodea.gymbookingsystem.dto.BookingRequest;
import volodea.gymbookingsystem.dto.BookingResponse;
import volodea.gymbookingsystem.entity.Booking;
import volodea.gymbookingsystem.entity.BookingStatus;
import volodea.gymbookingsystem.entity.GymClass;
import volodea.gymbookingsystem.exception.NoAvailableSpotsException;
import volodea.gymbookingsystem.exception.UserNotFoundException;
import volodea.gymbookingsystem.repository.BookingRepository;
import volodea.gymbookingsystem.repository.UserRepository;

import java.time.LocalDateTime;

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

        return new BookingResponse(
                booking.getId()
                , gymClass.getTitle()
                , gymClass.getStartTime()
                , booking.getBookingStatus()
                , booking.getCreatedAt()
        );
    }
}
