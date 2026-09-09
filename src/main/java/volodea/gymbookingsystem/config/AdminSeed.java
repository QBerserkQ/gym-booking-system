package volodea.gymbookingsystem.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import volodea.gymbookingsystem.entity.Role;
import volodea.gymbookingsystem.entity.User;
import volodea.gymbookingsystem.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class AdminSeed implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;


    @Override
    public void run(String... args) throws Exception {
        if(!userRepository.existsByEmail(email)){
            User user = User.builder()
                    .username("Volodea")
                    .role(Role.ADMIN)
                    .email(email)
                    .passwordHashed(passwordEncoder.encode(password))
                    .build();

            userRepository.save(user);
        }
    }
}
