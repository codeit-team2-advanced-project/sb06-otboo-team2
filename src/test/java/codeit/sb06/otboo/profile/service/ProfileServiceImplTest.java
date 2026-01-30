package codeit.sb06.otboo.profile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.exception.user.UserNotFoundException;
import codeit.sb06.otboo.profile.dto.ProfileDto;
import codeit.sb06.otboo.profile.entity.Profile;
import codeit.sb06.otboo.profile.repository.ProfileRepository;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Test
    void createBuildsProfileAndReturnsDto() {
        User user = new User(
            UUID.randomUUID(),
            "user@example.com",
            "name",
            Role.USER,
            false,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0),
            null,
            "password"
        );

        when(userRepository.findByEmail(eq("user@example.com"))).thenReturn(Optional.of(user));

        Profile savedProfile = new Profile(
            UUID.randomUUID(),
            "name",
            LocalDateTime.MIN,
            3,
            null,
            "UNSPECIFIED",
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0),
            List.of(),
            0,
            0,
            user
        );
        when(profileRepository.save(any(Profile.class))).thenReturn(savedProfile);

        ProfileDto result = profileService.create(user);

        verify(profileRepository).save(any(Profile.class));
        assertNotNull(result);
        assertEquals(user.getId(), result.userId());
        assertEquals("name", result.name());
    }

    @Test
    void createThrowsWhenUserNotFound() {
        User user = new User(
            UUID.randomUUID(),
            "user@example.com",
            "name",
            Role.USER,
            false,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0),
            null,
            "password"
        );

        when(userRepository.findByEmail(eq("user@example.com"))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> profileService.create(user));
    }
}
