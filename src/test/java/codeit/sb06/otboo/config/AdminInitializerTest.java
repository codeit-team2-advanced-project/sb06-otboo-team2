package codeit.sb06.otboo.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.entity.Provider;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminInitializer adminInitializer;

    @Test
    void runCreatesAdminWhenMissing() {
        when(userRepository.findByEmail("admin@admin.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("admin1!")).thenReturn("encoded-admin-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        adminInitializer.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("admin@admin.com", saved.getEmail());
        assertEquals(Role.ADMIN, saved.getRole());
        assertFalse(saved.isLocked());
        assertEquals("encoded-admin-password", saved.getPassword());
    }

    @Test
    void runUpdatesExistingAdmin() {
        User existing = new User(
            UUID.randomUUID(),
            "admin@admin.com",
            "legacy-admin",
            Provider.LOCAL,
            Role.USER,
            true,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0),
            null,
            "old-password",
            null,
            null
        );
        when(userRepository.findByEmail("admin@admin.com")).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("admin1!")).thenReturn("encoded-admin-password");

        adminInitializer.run();

        verify(userRepository).save(existing);
        verify(passwordEncoder).encode(eq("admin1!"));
        assertEquals(Role.ADMIN, existing.getRole());
        assertFalse(existing.isLocked());
        assertEquals("encoded-admin-password", existing.getPassword());
    }
}
