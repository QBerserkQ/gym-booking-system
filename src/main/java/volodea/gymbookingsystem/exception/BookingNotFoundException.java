package volodea.gymbookingsystem.exception;

public class BookingNotFoundException extends NotFoundException {
    public BookingNotFoundException(Long id) {
        super("Booking not found with id " + id);
    }
}
