package volodea.gymbookingsystem.exception;

public class InvalidCredentialsException extends UnauthorizedException {
    public InvalidCredentialsException() {
        super("Invalid mail or password");
    }
}
