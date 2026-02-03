package codeit.sb06.otboo.profile.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import codeit.sb06.otboo.profile.entity.Profile;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileDtoTest {

    @Test
    void fromMapsProfileFields() {
        User user = new User(
            UUID.randomUUID(),
            "user@example.com",
            "name",
            Role.USER,
            false,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0),
            null,
            "password",
            null,
            null
        );

        Profile profile = new Profile(
            UUID.randomUUID(),
            "name",
            LocalDateTime.of(2000, 1, 1, 0, 0),
            2,
            "image",
            "UNSPECIFIED",
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0),
            List.of("seoul"),
            1,
            2,
            user
        );

        ProfileDto dto = ProfileDto.from(profile);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.userId());
        assertEquals("name", dto.name());
        assertEquals("UNSPECIFIED", dto.gender());
        assertEquals(LocalDateTime.of(2000, 1, 1, 0, 0), dto.birthDate());
        assertEquals(List.of("seoul"), dto.locations());
        assertEquals(2, dto.temperatureSensitivity());
        assertEquals("image", dto.profileImageUrl());
    }
}
