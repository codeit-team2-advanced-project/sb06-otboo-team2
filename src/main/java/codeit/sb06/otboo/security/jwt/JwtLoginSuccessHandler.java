package codeit.sb06.otboo.security.jwt;

import codeit.sb06.otboo.dto.JwtDto;
import codeit.sb06.otboo.dto.JwtInformation;
import codeit.sb06.otboo.dto.response.ErrorResponse;
import codeit.sb06.otboo.security.OtbooUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if(authentication.getPrincipal() instanceof OtbooUserDetails userDetails) {
            try{
                String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
                String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

                Cookie refreshTokenCookie = jwtTokenProvider.generateRefreshTokenCookie(refreshToken);
                response.addCookie(refreshTokenCookie);

                JwtDto jwtDto = new JwtDto(
                    userDetails.getUserDto(),
                    accessToken
                );

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

                jwtRegistry.registerJwtInformation(
                    new JwtInformation(
                        userDetails.getUserDto(),
                        accessToken,
                        refreshToken
                    )
                );

                log.info("JWT 로그인 성공: userId={}", userDetails.getUserDto().id());
            } catch (JOSEException e) {
                log.error("Failed to generate JWT token for user: {}", userDetails.getUsername(), e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to generate JWT token");
                ErrorResponse errorResponse = new ErrorResponse(
                    new RuntimeException("Failed to generate JWT token")
                );
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            }
        }
    }
}
