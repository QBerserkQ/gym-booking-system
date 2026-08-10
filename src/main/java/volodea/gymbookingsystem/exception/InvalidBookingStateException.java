package volodea.gymbookingsystem.exception;

public class InvalidBookingStateException extends RuntimeException {
    public InvalidBookingStateException(Long bookingId) {
        super("Booking with id " + bookingId + " is already processed");
    }
}
