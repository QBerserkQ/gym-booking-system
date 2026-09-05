package volodea.gymbookingsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import volodea.gymbookingsystem.entity.RefreshToken;
import volodea.gymbookingsystem.entity.User;
import volodea.gymbookingsystem.exception.RefreshTokenExpiredException;
import volodea.gymbookingsystem.exception.RefreshTokenNotFoundException;
import volodea.gymbookingsystem.repository.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${refresh.expiration}")
    private long expDays;

    @Transactional
     public RefreshToken generateRefreshToken(User user) {
         refreshTokenRepository.deleteByUser(user);

         RefreshToken refreshToken = RefreshToken.builder()
                 .token(UUID.randomUUID().toString())
                 .expiryDate(LocalDateTime.now().plusDays(expDays))
                 .user(user)
                 .build();

         return refreshTokenRepository.save(refreshToken);
     }

     @Transactional
     public RefreshToken verifyRefreshToken(String token) {
         RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                 .orElseThrow(() -> new RefreshTokenNotFoundException(token));

        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenExpiredException(refreshToken.getExpiryDate());
        }

         return refreshToken;
     }

     @Transactional
     public void deleteRefreshTokenByUser(User user) {
         refreshTokenRepository.deleteByUser(user);
     }
}
