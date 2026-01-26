package codeit.sb06.otboo.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import codeit.sb06.otboo.dto.UserDto;
import codeit.sb06.otboo.security.OtbooUserDetails;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    @Test
    void generatesAndValidatesAccessToken() throws Exception {
        JwtTokenProvider provider = new JwtTokenProvider(
            "access-secret-01234567890123456789012345678901",
            60_000,
            "refresh-secret-01234567890123456789012345678901",
            120_000
        );

        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "user@example.com",
            "User",
            null,
            "USER",
            false
        );
        OtbooUserDetails userDetails = new OtbooUserDetails(userDto, "password");

        String accessToken = provider.generateAccessToken(userDetails);

        assertTrue(provider.validateAccessToken(accessToken));
        assertFalse(provider.validateRefreshToken(accessToken));
        assertEquals("user@example.com", provider.getUserNameFromToken(accessToken));
        assertEquals(userDto.id(), provider.getUserId(accessToken));
        assertNotNull(provider.getTokenId(accessToken));
    }

    @Test
    void generatesAndValidatesRefreshToken() throws Exception {
        JwtTokenProvider provider = new JwtTokenProvider(
            "access-secret-01234567890123456789012345678901",
            60_000,
            "refresh-secret-01234567890123456789012345678901",
            120_000
        );

        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "refresh@example.com",
            "User",
            null,
            "USER",
            false
        );
        OtbooUserDetails userDetails = new OtbooUserDetails(userDto, "password");

        String refreshToken = provider.generateRefreshToken(userDetails);

        assertTrue(provider.validateRefreshToken(refreshToken));
        assertFalse(provider.validateAccessToken(refreshToken));
        assertEquals("refresh@example.com", provider.getUserNameFromToken(refreshToken));
        assertEquals(userDto.id(), provider.getUserId(refreshToken));
    }

    @Test
    void invalidTokenFailsValidation() throws Exception {
        JwtTokenProvider providerA = new JwtTokenProvider(
            "access-secret-01234567890123456789012345678901",
            60_000,
            "refresh-secret-01234567890123456789012345678901",
            120_000
        );
        JwtTokenProvider providerB = new JwtTokenProvider(
            "different-access-secret-01234567890123456789012345678901",
            60_000,
            "different-refresh-secret-01234567890123456789012345678901",
            120_000
        );

        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "user@example.com",
            "User",
            null,
            "USER",
            false
        );
        OtbooUserDetails userDetails = new OtbooUserDetails(userDto, "password");

        String accessToken = providerA.generateAccessToken(userDetails);

        assertFalse(providerB.validateAccessToken(accessToken));
    }

    @Test
    void returnsNullForMalformedToken() throws Exception {
        JwtTokenProvider provider = new JwtTokenProvider(
            "access-secret-01234567890123456789012345678901",
            60_000,
            "refresh-secret-01234567890123456789012345678901",
            120_000
        );

        assertNull(provider.getUserId("not-a-jwt"));
        assertNull(provider.getUserNameFromToken("not-a-jwt"));
        assertNull(provider.getTokenId("not-a-jwt"));
    }

    @Test
    void createsRefreshTokenCookies() throws Exception {
        JwtTokenProvider provider = new JwtTokenProvider(
            "access-secret-01234567890123456789012345678901",
            60_000,
            "refresh-secret-01234567890123456789012345678901",
            120_000
        );

        assertEquals("refreshToken", provider.generateRefreshTokenCookie("token").getName());
        assertEquals(0, provider.generateRefreshTokenExpirationCookie().getMaxAge());
    }
}
