package codeit.sb06.otboo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import codeit.sb06.otboo.security.user.OtbooUserDetails;
import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.user.entity.Provider;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.entity.User;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OtbooUserDetailsTest {

    @Test
    void fromBuildsUserDetailsFromUserEntity() {
        User user = new User(
            UUID.randomUUID(),
            "user@example.com",
            "tester",
            Provider.LOCAL,
            Role.ADMIN,
            false,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0),
            null,
            "encoded-password",
            null,
            null
        );

        OtbooUserDetails details = OtbooUserDetails.from(user);

        assertEquals("user@example.com", details.getUsername());
        assertEquals("encoded-password", details.getPassword());
        assertEquals("ROLE_ADMIN", details.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void returnsAuthorityAndCredentialFields() {
        UserDto dto = new UserDto(
            UUID.randomUUID(),
            "member@example.com",
            LocalDateTime.of(2026, 1, 1, 0, 0),
            Role.USER.name(),
            false
        );
        OtbooUserDetails details = new OtbooUserDetails(dto, "pw", Map.of());

        assertEquals("member@example.com", details.getUsername());
        assertEquals("pw", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void fromWithAttributesKeepsOAuth2AttributesAndName() {
        UUID userId = UUID.randomUUID();
        User user = new User(
            userId,
            "oauth@example.com",
            "oauth-user",
            Provider.KAKAO,
            Role.USER,
            false,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0),
            null,
            "encoded-password",
            null,
            null
        );

        Map<String, Object> attributes = Map.of("id", 12345L, "nickname", "oauth-nick");

        OtbooUserDetails details = OtbooUserDetails.from(user, attributes);

        assertEquals(attributes, details.getAttributes());
        assertEquals(userId.toString(), details.getName());
    }
}
