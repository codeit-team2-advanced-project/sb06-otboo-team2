package codeit.sb06.otboo.security;

import codeit.sb06.otboo.exception.auth.InvalidUserDetailException;
import java.util.UUID;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class)
            && UUID.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
            || authentication instanceof AnonymousAuthenticationToken
            || !authentication.isAuthenticated()) {
            throw new InvalidUserDetailException();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof OtbooUserDetails userDetails) {
            return userDetails.getUserDto().id();
        }

        throw new InvalidUserDetailException();
    }
}
