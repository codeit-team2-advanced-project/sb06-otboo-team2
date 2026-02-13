package codeit.sb06.otboo.security.handler;

import codeit.sb06.otboo.security.jwt.AbstractJwtSuccessHandler;
import codeit.sb06.otboo.security.jwt.JwtRegistry;
import codeit.sb06.otboo.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class OAuth2SuccessHandler extends AbstractJwtSuccessHandler {

    public OAuth2SuccessHandler(
        JwtTokenProvider jwtTokenProvider,
        JwtRegistry jwtRegistry,
        ObjectMapper objectMapper
    ) {
        super(jwtTokenProvider, jwtRegistry, objectMapper);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain, Authentication authentication) throws IOException, ServletException {
        onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        handleSuccess(request, response, authentication, true, false, "OAuth2 로그인 성공");
    }
}
