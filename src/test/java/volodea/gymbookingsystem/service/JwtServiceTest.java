package volodea.gymbookingsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import volodea.gymbookingsystem.config.jwt.JwtService;
import volodea.gymbookingsystem.entity.Role;
import volodea.gymbookingsystem.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "ZmFrZS1zZWNyZXQta2V5LWZvci10ZXN0aW5nLW9ubHktMjU2Yml0cw==");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 1800000L);
    }

    @Test
    void shouldGenerateTokenAndExtractUserIdCorrectly() {
        User testUser = User.builder().id(1L).role(Role.USER).build();

        String token = jwtService.generateJwtToken(testUser);

        assertThat(token).isNotNull();
        assertThat(jwtService.getUserIdFromToken(token)).isEqualTo(testUser.getId());
    }

    @Test
    void shouldValidateGeneratedTokenAsTrue(){
        User testUser = User.builder().id(1L).role(Role.USER).build();

        String token = jwtService.generateJwtToken(testUser);

        assertThat(jwtService.validateJwtToken(token)).isTrue();
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        assertThat(jwtService.validateJwtToken("not.a.valid.token")).isFalse();
    }
}
