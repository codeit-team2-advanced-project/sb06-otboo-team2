package codeit.sb06.otboo.security.jwt;

import codeit.sb06.otboo.dto.UserDto;
import codeit.sb06.otboo.security.OtbooUserDetails;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final int accessTokenExpirationMs;
    private final int refreshTokenExpirationMs;

    private final JWSSigner accessTokenSigner;
    private final JWSSigner refreshTokenSigner;
    private final JWSVerifier accessTokenVerifier;
    private final JWSVerifier refreshTokenVerifier;

    public JwtTokenProvider(
        @Value("${otboo.jwt.access-token.secret}") String accessTokenSecret,
        @Value("${otboo.jwt.access-token.expiration-ms}") int accessTokenExpirationMs,
        @Value("${otboo.jwt.refresh-token.secret}") String refreshTokenSecret,
        @Value("${otboo.jwt.refresh-token.expiration-ms}") int refreshTokenExpirationMs)
        throws JOSEException {
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;

        byte[] accessSecretBytes = accessTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenSigner = new MACSigner(accessSecretBytes);
        this.accessTokenVerifier = new MACVerifier(accessSecretBytes);

        byte[] refreshSecretBytes = refreshTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshTokenSigner = new MACSigner(refreshSecretBytes);
        this.refreshTokenVerifier = new MACVerifier(refreshSecretBytes);
    }

    public String generateAccessToken(OtbooUserDetails userDetails) throws JOSEException {
        return generateToken (userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
    }

    public String generateRefreshToken(OtbooUserDetails userDetails) throws JOSEException {
        return generateToken (userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, accessTokenVerifier, "access");
    }
    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshTokenVerifier, "refresh");
    }

    private boolean validateToken(String token, JWSVerifier verifier, String expectedType) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if(!signedJWT.verify(verifier)){
                log.debug("JWT signature verification failed for {} token", expectedType);
                return false;
            }

            String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
            if( !expectedType.equals(tokenType)) {
                log.debug("JWT token type mismatch: expected={}, actual={}", expectedType, tokenType);
                return false;
            }

            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if( expirationTime == null || expirationTime.before(new Date())) {
                log.debug("JWT token expired for {} token", expectedType);
                return false;
            }
            return true;
        } catch (JOSEException | ParseException e) {
            log.debug("Failed to validate {} token: {}", expectedType, e.getMessage());
            return false;
        }
    }

    public String getUserNameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            log.debug("Failed to parse JWT token: {}", e.getMessage());
            return null;
        }
    }

    public String getTokenId(String token){
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getJWTID();
        } catch (ParseException e) {
            log.debug("Failed to parse JWT token: {}", e.getMessage());
            return null;
        }
    }

    public UUID getUserId(String token){
        try{
            SignedJWT signedJWT = SignedJWT.parse(token);
            String userIdStr = (String) signedJWT.getJWTClaimsSet().getClaim("userId");
            if(userIdStr == null){
                throw new IllegalArgumentException("JWT claims set is null");
            }
            return UUID.fromString(userIdStr);
        } catch (ParseException e) {
            log.debug("Failed to parse JWT token: {}", e.getMessage());
            return null;
        }
    }

    private String generateToken(OtbooUserDetails userDetails, int expirationMs, JWSSigner signer,
        String tokenType) throws JOSEException {
        String tokenId = UUID.randomUUID().toString();
        UserDto user = userDetails.getUserDto();

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(user.email())
            .jwtID(tokenId)
            .claim("userId", user.id().toString())
            .claim("type", tokenType)
            .claim("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()))
            .issueTime(now)
            .expirationTime(expiration)
            .build();

        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader(JWSAlgorithm.HS256),
            claimsSet
        );

        signedJWT.sign(signer);
        String token = signedJWT.serialize();

        log.debug("Generated {} token for user {}", tokenType, user.email());

        return token;
    }

    public Cookie generateRefreshTokenCookie(String refreshToken) {
        Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(refreshTokenExpirationMs / 1000);
        return refreshCookie;
    }

    public Cookie generateRefreshTokenExpirationCookie() {
        Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        return refreshCookie;
    }

}
