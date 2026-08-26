package volodea.gymbookingsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import volodea.gymbookingsystem.dto.RegisterRequest;
import volodea.gymbookingsystem.dto.RegisterResponse;
import volodea.gymbookingsystem.entity.Role;
import volodea.gymbookingsystem.entity.User;
import volodea.gymbookingsystem.exception.EmailAlreadyExistsException;
import volodea.gymbookingsystem.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse registerUser(RegisterRequest registerRequest) {

        if(userRepository.existsByEmail(registerRequest.email())){
            throw new EmailAlreadyExistsException(registerRequest.email());
        }

        User user = User.builder()
                .username(registerRequest.username())
                .email(registerRequest.email())
                .passwordHashed(passwordEncoder.encode(registerRequest.password()))
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        return new RegisterResponse(user.getUsername(), user.getEmail());
    }
}
