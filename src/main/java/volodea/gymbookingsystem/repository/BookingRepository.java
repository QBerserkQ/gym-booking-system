package volodea.gymbookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import volodea.gymbookingsystem.entity.Booking;
import volodea.gymbookingsystem.entity.BookingStatus;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    long countByGymClassIdAndBookingStatus(Long id, BookingStatus status);
    List<Booking> findByBookingStatus(BookingStatus status);
    List<Booking> findByUserId(Long userId);
}
