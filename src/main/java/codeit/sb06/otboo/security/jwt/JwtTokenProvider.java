package codeit.sb06.otboo.security.jwt;

import com.nimbusds.jose.JWSSigner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final int accessTokenExpirationMs;
    private final int refreshTokenExpirationMs;

    private final JWSSigner accessTokenSigner;
    private final JWSSigner refreshTokenSigner;
    private final JWTVerifier accessTokenVerifier;
    private final JWTVerifier refreshTokenVerifier;



}
