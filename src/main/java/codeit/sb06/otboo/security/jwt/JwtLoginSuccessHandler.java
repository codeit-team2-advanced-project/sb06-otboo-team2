package codeit.sb06.otboo.security.jwt;

import codeit.sb06.otboo.security.AbstractJwtSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtLoginSuccessHandler extends AbstractJwtSuccessHandler {

    public JwtLoginSuccessHandler(
        ObjectMapper objectMapper,
        JwtTokenProvider jwtTokenProvider,
        JwtRegistry jwtRegistry
    ) {
        super(jwtTokenProvider, jwtRegistry, objectMapper);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        handleSuccess(request, response, authentication, false, true, "JWT 로그인 성공");
    }
}
