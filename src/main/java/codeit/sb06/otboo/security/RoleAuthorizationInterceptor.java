package codeit.sb06.otboo.security;

import codeit.sb06.otboo.exception.auth.ForbiddenException;
import codeit.sb06.otboo.exception.auth.InvalidUserDetailException;
import codeit.sb06.otboo.user.entity.Role;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RoleAuthorizationInterceptor implements HandlerInterceptor {

    private final RoleHierarchy roleHierarchy;

    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request,
        jakarta.servlet.http.HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }

        if (requireRole == null || requireRole.value().length == 0) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
            || authentication instanceof AnonymousAuthenticationToken
            || !authentication.isAuthenticated()) {
            throw new InvalidUserDetailException();
        }

        Collection<? extends GrantedAuthority> reachableAuthorities =
            roleHierarchy.getReachableGrantedAuthorities(authentication.getAuthorities());

        Set<String> granted = reachableAuthorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        boolean allowed = Arrays.stream(requireRole.value())
            .map(Role::name)
            .map(roleName -> "ROLE_" + roleName)
            .anyMatch(granted::contains);

        if (!allowed) {
            throw new ForbiddenException();
        }

        return true;
    }
}
