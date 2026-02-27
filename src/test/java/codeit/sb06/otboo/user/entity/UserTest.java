package codeit.sb06.otboo.user.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import codeit.sb06.otboo.user.dto.request.UserCreateRequest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserTest {

    @Test
    void fromInitializesDefaults() {
        UserCreateRequest request = new UserCreateRequest("name", "user@example.com", "pass");

        User user = User.from(request);

        assertEquals("user@example.com", user.getEmail());
        assertEquals("name", user.getName());
        assertEquals(Role.USER, user.getRole());
        assertFalse(user.isLocked());
        assertEquals(LocalDateTime.class, user.getCreatedAt().getClass());
        assertNull(user.getTemporaryPasswordHash());
        assertNull(user.getTemporaryPasswordExpiresAt());
    }

    @Test
    void setEncryptPasswordAndRoleAndLockAreUpdated() {
        User user = User.from(new UserCreateRequest("name", "user@example.com", "pass"));
        PasswordEncoder encoder = new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return "encoded-" + rawPassword;
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return false;
            }
        };

        user.setEncryptPassword(encoder, "raw");
        user.changeRole(Role.ADMIN);
        user.changeLockStatus(true);

        assertEquals("encoded-raw", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
        assertTrue(user.isLocked());
    }

    @Test
    void temporaryPasswordLifecycleWorks() {
        User user = User.from(new UserCreateRequest("name", "user@example.com", "pass"));
        LocalDateTime now = LocalDateTime.of(2026, 2, 4, 0, 0);

        assertFalse(user.isTemporaryPasswordValidAt(now));

        user.updateTempPassword("hash", now.plusMinutes(10));
        assertTrue(user.isTemporaryPasswordValidAt(now));
        assertFalse(user.isTemporaryPasswordValidAt(now.plusMinutes(11)));

        user.clearTemporaryPassword();
        assertNull(user.getTemporaryPasswordHash());
        assertNull(user.getTemporaryPasswordExpiresAt());
        assertFalse(user.isTemporaryPasswordValidAt(now));
    }
}
