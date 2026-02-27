package codeit.sb06.otboo.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import codeit.sb06.otboo.user.dto.request.UserSliceRequest;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.entity.Provider;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.domain.Slice;

@DataJpaTest
@EntityScan(basePackageClasses = User.class)
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
class UserRepositoryImplTest {

    @Autowired
    private UserRepository userRepository;

    private LocalDateTime baseTime;
    private List<User> savedUsers;

    @BeforeEach
    void setUp() {
        baseTime = LocalDateTime.of(2026, 1, 1, 0, 0);

        List<User> users = List.of(
            user("alpha@example.com", Role.USER, false, baseTime.minusDays(4)),
            user("beta@example.com", Role.USER, false, baseTime.minusDays(3)),
            user("gamma@example.com", Role.ADMIN, false, baseTime.minusDays(2)),
            user("delta@example.com", Role.USER, true, baseTime.minusDays(1))
        );
        savedUsers = userRepository.saveAll(users);
    }

    @Test
    void filtersByEmailRoleAndLockedAndAppliesPaging() {
        UserSliceRequest request = new UserSliceRequest(
            null,
            null,
            2,
            "createdAt",
            "DESC",
            "example",
            "USER",
            false
        );

        Slice<User> result = userRepository.findUsersBySlice(request);

        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream()
            .allMatch(user -> user.getRole() == Role.USER && !user.isLocked()));
        assertFalse(result.hasNext());
    }

    @Test
    void appliesCursorForEmailSort() {
        UserSliceRequest request = new UserSliceRequest(
            "c",
            null,
            10,
            "email",
            "ASC",
            null,
            null,
            null
        );

        Slice<User> result = userRepository.findUsersBySlice(request);

        assertFalse(result.getContent().isEmpty());
        assertTrue(result.getContent().stream()
            .allMatch(user -> user.getEmail().compareToIgnoreCase("c") > 0));
    }

    @Test
    void appliesCursorForUpdatedAtSort() {
        String cursor = baseTime.minusDays(3).toString();
        UserSliceRequest request = new UserSliceRequest(
            cursor,
            null,
            10,
            "updatedAt",
            "ASC",
            null,
            null,
            null
        );

        Slice<User> result = userRepository.findUsersBySlice(request);

        assertFalse(result.getContent().isEmpty());
        assertTrue(result.getContent().stream()
            .allMatch(user -> user.getUpdatedAt().isAfter(baseTime.minusDays(3))));
    }

    @Test
    void appliesCursorForRoleSort() {
        UserSliceRequest request = new UserSliceRequest(
            "ADMIN",
            null,
            10,
            "role",
            "ASC",
            null,
            null,
            null
        );

        Slice<User> result = userRepository.findUsersBySlice(request);

        assertFalse(result.getContent().isEmpty());
        assertTrue(result.getContent().stream()
            .allMatch(user -> user.getRole() == Role.USER));
    }

    @Test
    void appliesCursorForIdSort() {
        User target = savedUsers.stream()
            .max((a, b) -> a.getId().compareTo(b.getId()))
            .orElseThrow();
        UserSliceRequest request = new UserSliceRequest(
            target.getId().toString(),
            null,
            10,
            "id",
            "DESC",
            null,
            null,
            null
        );

        Slice<User> result = userRepository.findUsersBySlice(request);

        assertTrue(result.getContent().stream()
            .allMatch(user -> !user.getId().equals(target.getId())));
    }

    @Test
    void ignoresInvalidRoleAndIdAfter() {
        UserSliceRequest request = new UserSliceRequest(
            "invalid-date",
            "not-a-uuid",
            10,
            "createdAt",
            "DESC",
            null,
            "not-a-role",
            null
        );

        Slice<User> result = userRepository.findUsersBySlice(request);

        assertEquals(4, result.getContent().size());
    }

    @Test
    void appliesDefaultSortAndLimit() {
        UserSliceRequest request = new UserSliceRequest(
            null,
            null,
            0,
            null,
            null,
            null,
            null,
            null
        );

        Slice<User> result = userRepository.findUsersBySlice(request);

        assertEquals(4, result.getContent().size());
        assertEquals(4, result.getNumberOfElements());
    }

    private User user(String email, Role role, boolean locked, LocalDateTime createdAt) {
        return new User(
            null,
            email,
            "name",
            Provider.LOCAL,
            role,
            locked,
            createdAt,
            createdAt,
            null,
            "password",
            null,
            null
        );
    }

    @TestConfiguration
    static class QuerydslTestConfig {
        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
            return new JPAQueryFactory(entityManager);
        }
    }
}
