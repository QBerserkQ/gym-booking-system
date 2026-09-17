package volodea.gymbookingsystem.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import volodea.gymbookingsystem.entity.BookingStatus;
import volodea.gymbookingsystem.repository.BookingRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class StaleBookingConsumer {
    private final BookingRepository bookingRepository;

    @RabbitListener(queues = RabbitMQConfig.STALE_BOOKING_QUEUE)
    public void handleStaleBooking(Long bookingId) {
        bookingRepository.findById(bookingId).ifPresent(booking -> {
           if (booking.getBookingStatus() == BookingStatus.PENDING) {
               booking.setBookingStatus(BookingStatus.REJECTED);
               bookingRepository.save(booking);
               log.info("Auto-rejected booking with id: {}",  bookingId);
           }
        });
    }
}
