package volodea.gymbookingsystem.exception;

public class RefreshTokenNotFoundException extends NotFoundException {
    public RefreshTokenNotFoundException(String token) {
        super("Refresh token not found with token " + token);
    }
}
