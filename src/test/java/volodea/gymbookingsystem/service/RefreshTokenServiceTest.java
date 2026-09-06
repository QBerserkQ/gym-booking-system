package volodea.gymbookingsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import volodea.gymbookingsystem.entity.RefreshToken;
import volodea.gymbookingsystem.entity.User;
import volodea.gymbookingsystem.exception.RefreshTokenExpiredException;
import volodea.gymbookingsystem.exception.RefreshTokenNotFoundException;
import volodea.gymbookingsystem.repository.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void shouldGenerateRefreshToken() {
        User user = User.builder().id(1L).build();

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(i -> i.getArgument(0));

        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user);

        verify(refreshTokenRepository).deleteByUser(user);
        assertThat(refreshToken.getUser().getId()).isEqualTo(user.getId());
        assertThat(refreshToken.getToken()).isNotNull();
    }

    @Test
    void shouldVerifyRefreshToken() {
        RefreshToken refreshToken = RefreshToken.builder()
                .token("token")
                .expiryDate(LocalDateTime.now().plusDays(5)).build();

        when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.of(refreshToken));
        verify(refreshTokenRepository, never()).delete(any());

        RefreshToken verifiedToken = refreshTokenService.verifyRefreshToken("token");
        assertThat(verifiedToken).isNotNull();
        assertThat(verifiedToken.getToken()).isEqualTo("token");
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenNotFound() {
        when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.empty());

        assertThrows(RefreshTokenNotFoundException.class, () -> refreshTokenService.verifyRefreshToken("token"));
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenExpired() {
        RefreshToken expiredToken = RefreshToken.builder()
                .expiryDate(LocalDateTime.now().minusDays(100))
                .build();

        when(refreshTokenRepository.findByToken("token")).thenReturn(
                Optional.of(expiredToken));

        assertThrows(RefreshTokenExpiredException.class, () -> refreshTokenService.verifyRefreshToken("token"));
        verify(refreshTokenRepository).delete(expiredToken);
    }

    @Test
    void shouldDeleteRefreshTokenByUser() {
        User user = User.builder().id(1L).build();

        refreshTokenService.deleteRefreshTokenByUser(user);

        verify(refreshTokenRepository).deleteByUser(user);
    }
}
