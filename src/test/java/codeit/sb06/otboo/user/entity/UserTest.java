package codeit.sb06.otboo.user.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import codeit.sb06.otboo.user.dto.request.UserCreateRequest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

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
    }
}
