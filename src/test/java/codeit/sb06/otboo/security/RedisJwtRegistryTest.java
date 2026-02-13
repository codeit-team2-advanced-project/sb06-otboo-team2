package codeit.sb06.otboo.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.notification.config.EmbeddedRedisConfig;
import codeit.sb06.otboo.security.dto.JwtInformation;
import codeit.sb06.otboo.security.jwt.JwtTokenProvider;
import codeit.sb06.otboo.security.jwt.RedisJwtRegistry;
import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.user.entity.Role;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataRedisTest
@Import(EmbeddedRedisConfig.class)
@ActiveProfiles("test")
class RedisJwtRegistryTest {

    private static final byte[] TEST_SECRET =
        "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @AfterEach
    void tearDown() {
        Set<String> keys = redisTemplate.keys("jwt:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    @DisplayName("maxActiveJwtCount=1이면 기존 토큰이 제거된다")
    void evictsOldTokensWhenLimitReached() throws JOSEException {
        RedisJwtRegistry registry = new RedisJwtRegistry(redisTemplate, jwtTokenProvider, 1);
        UserDto userDto = createUserDto();

        TokenFixture first = createTokenFixture(userDto.id());
        TokenFixture second = createTokenFixture(userDto.id());
        stubTokenId(first);
        stubTokenId(second);

        registry.registerJwtInformation(new JwtInformation(userDto, first.accessToken, first.refreshToken));
        registry.registerJwtInformation(new JwtInformation(userDto, second.accessToken, second.refreshToken));

        assertFalse(registry.hasActiveJwtInformationByAccessToken(first.accessToken));
        assertFalse(registry.hasActiveJwtInformationByRefreshToken(first.refreshToken));
        assertTrue(registry.hasActiveJwtInformationByAccessToken(second.accessToken));
        assertTrue(registry.hasActiveJwtInformationByRefreshToken(second.refreshToken));
    }

    @Test
    @DisplayName("refresh rotate 시 기존 토큰은 비활성화되고 새 토큰이 활성화된다")
    void rotatesRefreshToken() throws JOSEException {
        RedisJwtRegistry registry = new RedisJwtRegistry(redisTemplate, jwtTokenProvider, 2);
        UserDto userDto = createUserDto();

        TokenFixture oldTokens = createTokenFixture(userDto.id());
        TokenFixture newTokens = createTokenFixture(userDto.id());
        stubTokenId(oldTokens);
        stubTokenId(newTokens);

        registry.registerJwtInformation(new JwtInformation(userDto, oldTokens.accessToken, oldTokens.refreshToken));
        registry.rotateJwtInformation(
            oldTokens.refreshToken,
            new JwtInformation(userDto, newTokens.accessToken, newTokens.refreshToken)
        );

        assertFalse(registry.hasActiveJwtInformationByAccessToken(oldTokens.accessToken));
        assertFalse(registry.hasActiveJwtInformationByRefreshToken(oldTokens.refreshToken));
        assertTrue(registry.hasActiveJwtInformationByAccessToken(newTokens.accessToken));
        assertTrue(registry.hasActiveJwtInformationByRefreshToken(newTokens.refreshToken));
    }

    @Test
    @DisplayName("userId 기준 무효화 시 해당 유저 토큰이 모두 제거된다")
    void invalidatesJwtInformationByUserId() throws JOSEException {
        RedisJwtRegistry registry = new RedisJwtRegistry(redisTemplate, jwtTokenProvider, 2);
        UserDto userDto = createUserDto();

        TokenFixture tokenFixture = createTokenFixture(userDto.id());
        stubTokenId(tokenFixture);

        registry.registerJwtInformation(
            new JwtInformation(userDto, tokenFixture.accessToken, tokenFixture.refreshToken)
        );
        assertTrue(registry.hasActiveJwtInformationByUserId(userDto.id()));

        registry.invalidateJwtInformationByUserId(userDto.id());

        assertFalse(registry.hasActiveJwtInformationByUserId(userDto.id()));
        assertFalse(registry.hasActiveJwtInformationByAccessToken(tokenFixture.accessToken));
        assertFalse(registry.hasActiveJwtInformationByRefreshToken(tokenFixture.refreshToken));
    }

    private void stubTokenId(TokenFixture fixture) {
        when(jwtTokenProvider.getTokenId(fixture.accessToken)).thenReturn(fixture.accessTokenId);
        when(jwtTokenProvider.getTokenId(fixture.refreshToken)).thenReturn(fixture.refreshTokenId);
    }

    private UserDto createUserDto() {
        return new UserDto(
            UUID.randomUUID(),
            "user@example.com",
            LocalDateTime.now(),
            Role.USER.name(),
            false
        );
    }

    private TokenFixture createTokenFixture(UUID userId) throws JOSEException {
        String accessTokenId = UUID.randomUUID().toString();
        String refreshTokenId = UUID.randomUUID().toString();
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3_600_000L);

        String accessToken = createSignedToken(accessTokenId, userId, expiration, "access");
        String refreshToken = createSignedToken(refreshTokenId, userId, expiration, "refresh");
        return new TokenFixture(accessToken, refreshToken, accessTokenId, refreshTokenId);
    }

    private String createSignedToken(
        String tokenId,
        UUID userId,
        Date expiration,
        String tokenType
    ) throws JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .jwtID(tokenId)
            .subject("user@example.com")
            .claim("userId", userId.toString())
            .claim("type", tokenType)
            .issueTime(new Date())
            .expirationTime(expiration)
            .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(new MACSigner(TEST_SECRET));
        return signedJWT.serialize();
    }

    private record TokenFixture(
        String accessToken,
        String refreshToken,
        String accessTokenId,
        String refreshTokenId
    ) {
    }
}
