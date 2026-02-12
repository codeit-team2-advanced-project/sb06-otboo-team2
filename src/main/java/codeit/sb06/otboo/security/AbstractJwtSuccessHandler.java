package codeit.sb06.otboo.security;

import codeit.sb06.otboo.exception.ErrorResponse;
import codeit.sb06.otboo.security.dto.JwtDto;
import codeit.sb06.otboo.security.dto.JwtInformation;
import codeit.sb06.otboo.security.jwt.JwtRegistry;
import codeit.sb06.otboo.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Slf4j
public abstract class AbstractJwtSuccessHandler implements AuthenticationSuccessHandler {

    protected final JwtTokenProvider jwtTokenProvider;
    protected final JwtRegistry jwtRegistry;
    protected final ObjectMapper objectMapper;

    protected AbstractJwtSuccessHandler(
        JwtTokenProvider jwtTokenProvider,
        JwtRegistry jwtRegistry,
        ObjectMapper objectMapper
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtRegistry = jwtRegistry;
        this.objectMapper = objectMapper;
    }

    protected void handleSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication,
        boolean unauthorizedOnInvalidPrincipal,
        boolean redirectOnOauth2Callback,
        String successLogLabel
    ) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (!(authentication.getPrincipal() instanceof OtbooUserDetails userDetails)) {
            if (unauthorizedOnInvalidPrincipal) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            return;
        }

        try {
            String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            Cookie refreshTokenCookie = jwtTokenProvider.generateRefreshTokenCookie(refreshToken);
            response.addCookie(refreshTokenCookie);

            JwtDto jwtDto = new JwtDto(userDetails.getUserDto(), accessToken);

            response.setStatus(HttpServletResponse.SC_OK);
            jwtRegistry.registerJwtInformation(
                new JwtInformation(userDetails.getUserDto(), accessToken, refreshToken)
            );

            if (redirectOnOauth2Callback && isOauth2Callback(request)) {
                String contextPath = request.getContextPath();
                response.sendRedirect((contextPath == null ? "" : contextPath) + "/");
                return;
            }

            response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
            log.info("{}: userId={}", successLogLabel, userDetails.getUserDto().id());
        } catch (JOSEException e) {
            log.error("Failed to generate JWT token for user: {}", userDetails.getUsername(), e);
            ErrorResponse errorResponse = new ErrorResponse(
                new RuntimeException("Failed to generate JWT token")
            );
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    private boolean isOauth2Callback(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && uri.startsWith("/login/oauth2/code/");
    }
}
