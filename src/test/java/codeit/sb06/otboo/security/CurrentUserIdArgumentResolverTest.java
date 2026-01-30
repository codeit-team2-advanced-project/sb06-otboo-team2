package codeit.sb06.otboo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import codeit.sb06.otboo.exception.auth.InvalidUserDetailException;
import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.user.entity.Role;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

class CurrentUserIdArgumentResolverTest {

    private final CurrentUserIdArgumentResolver resolver = new CurrentUserIdArgumentResolver();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void resolvesUserIdFromAuthenticatedPrincipal() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDto userDto = new UserDto(
            userId,
            "user@example.com",
            LocalDateTime.of(2026, 1, 1, 0, 0),
            Role.USER.name(),
            false
        );
        OtbooUserDetails userDetails = new OtbooUserDetails(userDto, "password");
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, "password", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MethodParameter parameter = methodParameter("endpoint", UUID.class);
        Object resolved = resolver.resolveArgument(
            parameter,
            null,
            new ServletWebRequest(new MockHttpServletRequest(), new MockHttpServletResponse()),
            null
        );

        assertEquals(userId, resolved);
    }

    @Test
    void throwsWhenAuthenticationMissing() throws Exception {
        MethodParameter parameter = methodParameter("endpoint", UUID.class);
        assertThrows(InvalidUserDetailException.class, () ->
            resolver.resolveArgument(
                parameter,
                null,
                new ServletWebRequest(new MockHttpServletRequest(), new MockHttpServletResponse()),
                null
            )
        );
    }

    @Test
    void throwsWhenPrincipalIsNotUserDetails() throws Exception {
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken("user", "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MethodParameter parameter = methodParameter("endpoint", UUID.class);
        assertThrows(InvalidUserDetailException.class, () ->
            resolver.resolveArgument(
                parameter,
                null,
                new ServletWebRequest(new MockHttpServletRequest(), new MockHttpServletResponse()),
                null
            )
        );
    }

    private MethodParameter methodParameter(String methodName, Class<?> paramType) throws Exception {
        Method method = TestController.class.getDeclaredMethod(methodName, paramType);
        return new MethodParameter(method, 0);
    }

    static class TestController {
        public void endpoint(@CurrentUserId UUID userId) {}
    }
}
