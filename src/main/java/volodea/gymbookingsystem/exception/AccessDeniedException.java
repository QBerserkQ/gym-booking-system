package volodea.gymbookingsystem.exception;

public class AccessDeniedException extends ForbiddenException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
