package volodea.gymbookingsystem.exception;

public class NoAvailableSpotsException extends RuntimeException {
    public NoAvailableSpotsException(Long gymClassid) {
        super("No available spots for the gym with id: " + gymClassid);
    }
}
