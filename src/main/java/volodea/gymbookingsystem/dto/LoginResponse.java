package volodea.gymbookingsystem.dto;

public record LoginResponse(
        String jwtToken
        , String refreshToken
) {
}
