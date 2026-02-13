package codeit.sb06.otboo.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import codeit.sb06.otboo.exception.auth.ForbiddenException;
import codeit.sb06.otboo.exception.auth.InvalidUserDetailException;
import codeit.sb06.otboo.security.resolver.RequireRole;
import codeit.sb06.otboo.security.resolver.RoleAuthorizationInterceptor;
import codeit.sb06.otboo.user.entity.Role;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RoleAuthorizationInterceptorTest {

    private final RoleHierarchy roleHierarchy = RoleHierarchyImpl.withDefaultRolePrefix()
        .role(Role.ADMIN.name())
        .implies(Role.USER.name())
        .build();

    private final RoleAuthorizationInterceptor interceptor =
        new RoleAuthorizationInterceptor(roleHierarchy);

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void allowsAdminForUserRequirement() throws Exception {
        setAuthentication("ROLE_ADMIN");

        HandlerMethod handlerMethod = handlerMethod(TestController.class, "userEndpoint");
        boolean allowed = interceptor.preHandle(
            new MockHttpServletRequest(),
            new MockHttpServletResponse(),
            handlerMethod
        );

        assertTrue(allowed);
    }

    @Test
    void deniesUserForAdminRequirement() throws Exception {
        setAuthentication("ROLE_USER");

        HandlerMethod handlerMethod = handlerMethod(TestController.class, "adminEndpoint");
        assertThrows(ForbiddenException.class, () ->
            interceptor.preHandle(
                new MockHttpServletRequest(),
                new MockHttpServletResponse(),
                handlerMethod
            )
        );
    }

    @Test
    void deniesWhenAuthenticationMissing() throws Exception {
        HandlerMethod handlerMethod = handlerMethod(TestController.class, "userEndpoint");
        assertThrows(InvalidUserDetailException.class, () ->
            interceptor.preHandle(
                new MockHttpServletRequest(),
                new MockHttpServletResponse(),
                handlerMethod
            )
        );
    }

    @Test
    void allowsClassLevelRequirement() throws Exception {
        setAuthentication("ROLE_ADMIN");

        HandlerMethod handlerMethod = handlerMethod(AdminController.class, "noAnnotationEndpoint");
        boolean allowed = interceptor.preHandle(
            new MockHttpServletRequest(),
            new MockHttpServletResponse(),
            handlerMethod
        );

        assertTrue(allowed);
    }

    private void setAuthentication(String role) {
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                "user",
                "password",
                List.of(new SimpleGrantedAuthority(role))
            );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private HandlerMethod handlerMethod(Class<?> clazz, String methodName) throws Exception {
        Method method = clazz.getDeclaredMethod(methodName);
        return new HandlerMethod(clazz.getDeclaredConstructor().newInstance(), method);
    }

    static class TestController {
        @RequireRole(Role.USER)
        public void userEndpoint() {}

        @RequireRole(Role.ADMIN)
        public void adminEndpoint() {}
    }

    @RequireRole(Role.ADMIN)
    static class AdminController {
        public void noAnnotationEndpoint() {}
    }
}
