package codeit.sb06.otboo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.exception.user.LockedUserException;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.entity.Provider;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class OtbooUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OtbooUserDetailsService service;

    @Test
    void loadUserByUsernameReturnsDetailsWhenUserExists() {
        User user = user(false);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("user@example.com");

        assertInstanceOf(OtbooUserDetails.class, details);
        assertEquals("user@example.com", details.getUsername());
    }

    @Test
    void loadUserByUsernameThrowsWhenUserLocked() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user(true)));

        assertThrows(LockedUserException.class, () -> service.loadUserByUsername("user@example.com"));
    }

    @Test
    void loadUserByUsernameThrowsWhenUserMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
            () -> service.loadUserByUsername("missing@example.com"));
    }

    private User user(boolean locked) {
        return new User(
            UUID.randomUUID(),
            "user@example.com",
            "tester",
            Provider.LOCAL,
            Role.USER,
            locked,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0),
            null,
            "encoded-password",
            null,
            null
        );
    }
}
