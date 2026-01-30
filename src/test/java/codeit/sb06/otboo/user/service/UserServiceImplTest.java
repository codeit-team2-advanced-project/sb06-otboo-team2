package codeit.sb06.otboo.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.exception.user.UserAlreadyExistException;
import codeit.sb06.otboo.exception.user.UserNotFoundException;
import codeit.sb06.otboo.profile.service.ProfileServiceImpl;
import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.user.dto.request.UserCreateRequest;
import codeit.sb06.otboo.user.dto.request.UserSliceRequest;
import codeit.sb06.otboo.user.dto.response.UserDtoCursorResponse;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ProfileServiceImpl profileService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createSavesUserAndCreatesProfile() {
        UserCreateRequest request = new UserCreateRequest("name", "user@example.com", "pass");

        when(userRepository.findByEmail(eq("user@example.com"))).thenReturn(Optional.empty());
        when(passwordEncoder.encode(eq("pass"))).thenReturn("encoded");

        User saved = new User(
            UUID.randomUUID(),
            "user@example.com",
            "name",
            Role.USER,
            false,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0),
            null,
            "encoded"
        );
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserDto result = userService.create(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("encoded", captor.getValue().getPassword());

        verify(profileService).create(saved);
        assertEquals("user@example.com", result.email());
        assertEquals(Role.USER.name(), result.role());
        assertNotNull(result.id());
    }

    @Test
    void createThrowsWhenEmailExists() {
        UserCreateRequest request = new UserCreateRequest("name", "user@example.com", "pass");
        when(userRepository.findByEmail(eq("user@example.com")))
            .thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistException.class, () -> userService.create(request));
    }

    @Test
    void getUsersCursorBuildsResponse() {
        User userA = new User(
            UUID.randomUUID(),
            "a@example.com",
            "name-a",
            Role.USER,
            false,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0),
            null,
            "password"
        );
        User userB = new User(
            UUID.randomUUID(),
            "b@example.com",
            "name-b",
            Role.ADMIN,
            false,
            LocalDateTime.of(2026, 1, 2, 0, 0),
            LocalDateTime.of(2026, 1, 2, 0, 0),
            null,
            "password"
        );

        Slice<User> slice = new SliceImpl<>(
            List.of(userA, userB),
            PageRequest.of(0, 2),
            true
        );
        UserSliceRequest request = new UserSliceRequest(
            null,
            null,
            2,
            "createdAt",
            "DESC",
            null,
            null,
            null
        );

        when(userRepository.findUsersBySlice(eq(request))).thenReturn(slice);

        UserDtoCursorResponse response = userService.getUsersCursor(request);

        assertEquals(2, response.data().size());
        assertEquals("createdAt", response.sortBy());
        assertEquals("DESC", response.sortDirection());
        assertEquals(userB.getId().toString(), response.nextIdAfter());
        assertEquals(userB.getCreatedAt().toString(), response.nextCursor());
        assertEquals(2, response.totalCount());
        assertEquals(true, response.hasNext());
    }

    @Test
    void changeUserRoleUpdatesRoleAndSaves() {
        UUID userId = UUID.randomUUID();
        User user = new User(
            userId,
            "user@example.com",
            "name",
            Role.USER,
            false,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0),
            null,
            "password"
        );
        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.changeUserRole(userId, Role.ADMIN.name());

        assertEquals(Role.ADMIN.name(), result.role());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changeUserRoleThrowsWhenUserMissing() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
            () -> userService.changeUserRole(userId, Role.ADMIN.name()));
    }

    @Test
    void changeLockStatusUpdatesUserAndSaves() {
        UUID userId = UUID.randomUUID();
        User user = new User(
            userId,
            "user@example.com",
            "name",
            Role.USER,
            false,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0),
            null,
            "password"
        );
        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.changeLockStatus(userId, true);

        assertEquals(true, result.isLocked());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changeLockStatusThrowsWhenUserMissing() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
            () -> userService.changeLockStatus(userId, true));
    }
}
