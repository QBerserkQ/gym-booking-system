package volodea.gymbookingsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import volodea.gymbookingsystem.dto.UserRequest;
import volodea.gymbookingsystem.dto.UserResponse;
import volodea.gymbookingsystem.entity.User;
import volodea.gymbookingsystem.exception.UserNotFoundException;
import volodea.gymbookingsystem.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse updateUserRole(Long userId, UserRequest userRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setRole(userRequest.role());
        userRepository.save(user);

        return new UserResponse(user.getId(), user.getRole());
    }
}
