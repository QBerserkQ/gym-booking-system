package volodea.gymbookingsystem.exception;

import java.time.LocalDateTime;

public class RefreshTokenExpiredException extends UnauthorizedException {
    public RefreshTokenExpiredException(LocalDateTime expiryDate) {
        super("Refresh token expired " + expiryDate);
    }
}
