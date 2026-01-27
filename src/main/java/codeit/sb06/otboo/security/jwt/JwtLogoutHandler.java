package codeit.sb06.otboo.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {

        Cookie refreshTokenExpirationCookie = tokenProvider.generateRefreshTokenExpirationCookie();
        response.addCookie(refreshTokenExpirationCookie);

        Cookie[] cookies = request.getCookies();

        if(cookies == null) {
            log.debug("No cookies found in the request.");
            return;
        }

        Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals(refreshTokenExpirationCookie.getName()))
            .findFirst()
            .ifPresent(cookie -> {
                String refreshToken = cookie.getValue();
                UUID userId = tokenProvider.getUserId(refreshToken);
                if(userId == null) {
                    log.debug("Invalidating JWT information for user ID");
                    return;
                }
                jwtRegistry.invalidateJwtInformationByUserId(userId);
            });

        log.debug("JWT logout completed.");
    }
}
