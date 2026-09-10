package volodea.gymbookingsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import volodea.gymbookingsystem.dto.UserRequest;
import volodea.gymbookingsystem.dto.UserResponse;
import volodea.gymbookingsystem.entity.Role;
import volodea.gymbookingsystem.entity.User;
import volodea.gymbookingsystem.exception.UserNotFoundException;
import volodea.gymbookingsystem.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldUpdateUserRole() {
        User basicUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .email("email.com")
                .username("vova")
                .passwordHashed("123")
                .build();
        UserRequest userRequest = new UserRequest(Role.SUPPORT);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(basicUser));

        UserResponse response = userService.updateUserRole(1L, userRequest);

        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.role()).isEqualTo(Role.SUPPORT);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserRole(1L, new UserRequest(Role.USER)));
    }
}
