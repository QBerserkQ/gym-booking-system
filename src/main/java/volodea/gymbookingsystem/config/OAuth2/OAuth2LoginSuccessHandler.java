package volodea.gymbookingsystem.config.OAuth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import volodea.gymbookingsystem.entity.Role;
import volodea.gymbookingsystem.entity.User;
import volodea.gymbookingsystem.repository.UserRepository;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    @Value("${app.frontend.oauth2-redirect-uri}")
    private String frontendRedirectUri;

    private static final Duration CODE_TTL = Duration.ofSeconds(30);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request
            , HttpServletResponse response
            , Authentication authentication) throws IOException{

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(
                        () -> {
                            User newUser = User.builder()
                                    .email(email)
                                    .username(name)
                                    .passwordHashed("")
                                    .role(Role.USER)
                                    .build();

                            return userRepository.save(newUser);
                        }
                );

        String code = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("oauth2:code:" + code, user.getId().toString(), CODE_TTL);

        response.sendRedirect(frontendRedirectUri + "?code=" + code);
    }
}
