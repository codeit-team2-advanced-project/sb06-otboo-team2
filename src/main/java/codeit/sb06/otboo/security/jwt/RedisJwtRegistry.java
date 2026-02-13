package codeit.sb06.otboo.security.jwt;

import codeit.sb06.otboo.security.dto.JwtInformation;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.util.StringUtils;

@Slf4j
public class RedisJwtRegistry implements JwtRegistry {

    private static final String ACCESS_KEY_PREFIX = "jwt:access:";
    private static final String REFRESH_KEY_PREFIX = "jwt:refresh:";
    private static final String REFRESH_LINK_KEY_PREFIX = "jwt:link:refresh:";
    private static final String USER_REFRESH_INDEX_PREFIX = "jwt:user:";
    private static final String USER_REFRESH_INDEX_SUFFIX = ":refresh";

    private final StringRedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final int maxActiveJwtCount;

    public RedisJwtRegistry(
        StringRedisTemplate redisTemplate,
        JwtTokenProvider jwtTokenProvider,
        int maxActiveJwtCount
    ) {
        this.redisTemplate = redisTemplate;
        this.jwtTokenProvider = jwtTokenProvider;
        this.maxActiveJwtCount = maxActiveJwtCount;
    }

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();
        String accessToken = jwtInformation.getAccessToken();
        String refreshToken = jwtInformation.getRefreshToken();

        String accessTokenId = jwtTokenProvider.getTokenId(accessToken);
        String refreshTokenId = jwtTokenProvider.getTokenId(refreshToken);

        if (!StringUtils.hasText(accessTokenId) || !StringUtils.hasText(refreshTokenId)) {
            log.debug("Skip registerJwtInformation due to invalid token id");
            return;
        }

        long accessTtlMs = getRemainingTtlMs(accessToken);
        long refreshTtlMs = getRemainingTtlMs(refreshToken);
        if (accessTtlMs <= 0 || refreshTtlMs <= 0) {
            log.debug("Skip registerJwtInformation due to expired token");
            return;
        }

        String accessKey = accessKey(accessTokenId);
        String refreshKey = refreshKey(refreshTokenId);
        String refreshLinkKey = refreshLinkKey(refreshTokenId);
        String userRefreshIndexKey = userRefreshIndexKey(userId);

        redisTemplate.opsForValue().set(accessKey, userId.toString(), accessTtlMs, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(refreshKey, userId.toString(), refreshTtlMs, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue()
            .set(refreshLinkKey, accessTokenId, refreshTtlMs, TimeUnit.MILLISECONDS);
        redisTemplate.opsForZSet()
            .add(userRefreshIndexKey, refreshTokenId, System.currentTimeMillis());
        redisTemplate.expire(userRefreshIndexKey, refreshTtlMs, TimeUnit.MILLISECONDS);

        trimExcessSessions(userId);
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        String userRefreshIndexKey = userRefreshIndexKey(userId);
        Set<String> refreshTokenIds = redisTemplate.opsForZSet().range(userRefreshIndexKey, 0, -1);
        if (refreshTokenIds == null || refreshTokenIds.isEmpty()) {
            redisTemplate.delete(userRefreshIndexKey);
            return;
        }

        for (String refreshTokenId : refreshTokenIds) {
            deleteSessionByRefreshTokenId(userId, refreshTokenId);
        }
        redisTemplate.delete(userRefreshIndexKey);
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        String userRefreshIndexKey = userRefreshIndexKey(userId);
        Set<String> refreshTokenIds = redisTemplate.opsForZSet().range(userRefreshIndexKey, 0, -1);
        if (refreshTokenIds == null || refreshTokenIds.isEmpty()) {
            return false;
        }

        for (String refreshTokenId : new HashSet<>(refreshTokenIds)) {
            if (redisTemplate.hasKey(refreshKey(refreshTokenId))) {
                return true;
            }
            redisTemplate.opsForZSet().remove(userRefreshIndexKey, refreshTokenId);
        }

        return false;
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        String accessTokenId = jwtTokenProvider.getTokenId(accessToken);
        if (!StringUtils.hasText(accessTokenId)) {
            return false;
        }
        return redisTemplate.hasKey(accessKey(accessTokenId));
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        String refreshTokenId = jwtTokenProvider.getTokenId(refreshToken);
        if (!StringUtils.hasText(refreshTokenId)) {
            return false;
        }
        return redisTemplate.hasKey(refreshKey(refreshTokenId));
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        String oldRefreshTokenId = jwtTokenProvider.getTokenId(refreshToken);
        if (!StringUtils.hasText(oldRefreshTokenId)) {
            return;
        }

        UUID userId = newJwtInformation.getUserDto().id();
        String oldRefreshKey = refreshKey(oldRefreshTokenId);
        String tokenOwnerId = redisTemplate.opsForValue().get(oldRefreshKey);
        if (!userId.toString().equals(tokenOwnerId)) {
            return;
        }

        deleteSessionByRefreshTokenId(userId, oldRefreshTokenId);
        registerJwtInformation(newJwtInformation);
    }

    @Override
    public void clearExpiredJwtInformation() {
        // Token keys are stored with TTL, so expired entries are removed by Redis automatically.
    }

    private void trimExcessSessions(UUID userId) {
        if (maxActiveJwtCount <= 0) {
            return;
        }

        String userRefreshIndexKey = userRefreshIndexKey(userId);
        Long sessionCount = redisTemplate.opsForZSet().zCard(userRefreshIndexKey);
        if (sessionCount == null || sessionCount <= maxActiveJwtCount) {
            return;
        }

        long overflowCount = sessionCount - maxActiveJwtCount;
        Set<TypedTuple<String>> oldestSessions =
            redisTemplate.opsForZSet().rangeWithScores(userRefreshIndexKey, 0, overflowCount - 1);
        if (oldestSessions == null || oldestSessions.isEmpty()) {
            return;
        }

        for (TypedTuple<String> tuple : oldestSessions) {
            String refreshTokenId = tuple.getValue();
            if (!StringUtils.hasText(refreshTokenId)) {
                continue;
            }
            deleteSessionByRefreshTokenId(userId, refreshTokenId);
        }
    }

    private void deleteSessionByRefreshTokenId(UUID userId, String refreshTokenId) {
        String userRefreshIndexKey = userRefreshIndexKey(userId);
        String refreshLinkKey = refreshLinkKey(refreshTokenId);
        String accessTokenId = redisTemplate.opsForValue().get(refreshLinkKey);

        redisTemplate.delete(refreshKey(refreshTokenId));
        redisTemplate.delete(refreshLinkKey);
        redisTemplate.opsForZSet().remove(userRefreshIndexKey, refreshTokenId);

        if (StringUtils.hasText(accessTokenId)) {
            redisTemplate.delete(accessKey(accessTokenId));
        }
    }

    private long getRemainingTtlMs(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (signedJWT.getJWTClaimsSet().getExpirationTime() == null) {
                return 0;
            }
            long remaining = signedJWT.getJWTClaimsSet().getExpirationTime().getTime()
                - System.currentTimeMillis();
            return Math.max(0, remaining);
        } catch (ParseException e) {
            return 0;
        }
    }

    private String accessKey(String accessTokenId) {
        return ACCESS_KEY_PREFIX + accessTokenId;
    }

    private String refreshKey(String refreshTokenId) {
        return REFRESH_KEY_PREFIX + refreshTokenId;
    }

    private String refreshLinkKey(String refreshTokenId) {
        return REFRESH_LINK_KEY_PREFIX + refreshTokenId;
    }

    private String userRefreshIndexKey(UUID userId) {
        return USER_REFRESH_INDEX_PREFIX + userId + USER_REFRESH_INDEX_SUFFIX;
    }
}
