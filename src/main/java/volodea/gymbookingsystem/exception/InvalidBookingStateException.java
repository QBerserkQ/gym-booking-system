package volodea.gymbookingsystem.exception;

public class InvalidBookingStateException extends ConflictException {
    public InvalidBookingStateException(Long bookingId) {
        super("Booking with id " + bookingId + " is already processed");
    }
}
