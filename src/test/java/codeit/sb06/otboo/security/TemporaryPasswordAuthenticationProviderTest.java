package codeit.sb06.otboo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.exception.user.LockedUserException;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class TemporaryPasswordAuthenticationProviderTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TemporaryPasswordAuthenticationProvider provider;

    @Test
    void authenticateWithPrimaryPasswordSucceeds() {
        User user = user(false, null, null);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain-password", "encoded-password")).thenReturn(true);

        Authentication auth = provider.authenticate(
            new UsernamePasswordAuthenticationToken("user@example.com", "plain-password")
        );

        assertEquals("user@example.com", auth.getName());
        assertInstanceOf(OtbooUserDetails.class, auth.getPrincipal());
    }

    @Test
    void authenticateWithValidTemporaryPasswordSucceedsAndClearsTempCredentials() {
        User user = user(false, "encoded-temp", LocalDateTime.now().plusMinutes(1));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("temp-password", "encoded-password")).thenReturn(false);
        when(passwordEncoder.matches("temp-password", "encoded-temp")).thenReturn(true);

        Authentication auth = provider.authenticate(
            new UsernamePasswordAuthenticationToken("user@example.com", "temp-password")
        );

        assertEquals("user@example.com", auth.getName());
        assertNull(user.getTemporaryPasswordHash());
        assertNull(user.getTemporaryPasswordExpiresAt());
    }

    @Test
    void authenticateThrowsWhenTemporaryPasswordExpired() {
        User user = user(false, "encoded-temp", LocalDateTime.now().minusMinutes(1));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("temp-password", "encoded-password")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () ->
            provider.authenticate(new UsernamePasswordAuthenticationToken("user@example.com", "temp-password"))
        );
    }

    @Test
    void authenticateThrowsWhenUserLocked() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user(true, null, null)));

        assertThrows(LockedUserException.class, () ->
            provider.authenticate(new UsernamePasswordAuthenticationToken("user@example.com", "any"))
        );
    }

    @Test
    void authenticateThrowsWhenUserMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () ->
            provider.authenticate(new UsernamePasswordAuthenticationToken("missing@example.com", "any"))
        );
    }

    @Test
    void authenticateThrowsWhenNoUsablePasswordExists() {
        User user = new User(
            UUID.randomUUID(),
            "user@example.com",
            "tester",
            Role.USER,
            false,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0),
            null,
            null,
            null,
            null
        );
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThrows(BadCredentialsException.class, () ->
            provider.authenticate(new UsernamePasswordAuthenticationToken("user@example.com", null))
        );
    }

    @Test
    void supportsUsernamePasswordToken() {
        assertTrue(provider.supports(UsernamePasswordAuthenticationToken.class));
    }

    private User user(boolean locked, String tempHash, LocalDateTime tempExpiresAt) {
        return new User(
            UUID.randomUUID(),
            "user@example.com",
            "tester",
            Role.USER,
            locked,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0),
            null,
            "encoded-password",
            tempHash,
            tempExpiresAt
        );
    }
}
