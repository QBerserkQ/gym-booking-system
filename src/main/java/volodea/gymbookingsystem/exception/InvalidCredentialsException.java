package volodea.gymbookingsystem.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid mail or password");
    }
}
