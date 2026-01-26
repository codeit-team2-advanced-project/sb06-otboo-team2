package codeit.sb06.otboo.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;



}
