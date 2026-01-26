package codeit.sb06.otboo.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.dto.JwtInformation;
import codeit.sb06.otboo.dto.UserDto;
import codeit.sb06.otboo.security.jwt.InMemoryJwtRegistry;
import codeit.sb06.otboo.security.jwt.JwtTokenProvider;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemoryJwtRegistryTest {

    @Test
    void evictsOldTokensWhenLimitReached() {
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        InMemoryJwtRegistry registry = new InMemoryJwtRegistry(1, tokenProvider);

        UserDto userDto = new UserDto(UUID.randomUUID(), "user@example.com", "User", null, "USER", false);
        JwtInformation first = new JwtInformation(userDto, "access-1", "refresh-1");
        JwtInformation second = new JwtInformation(userDto, "access-2", "refresh-2");

        registry.registerJwtInformation(first);
        registry.registerJwtInformation(second);

        assertFalse(registry.hasActiveJwtInformationByAccessToken("access-1"));
        assertTrue(registry.hasActiveJwtInformationByAccessToken("access-2"));
    }

    @Test
    void rotatesRefreshToken() {
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        InMemoryJwtRegistry registry = new InMemoryJwtRegistry(2, tokenProvider);

        UserDto userDto = new UserDto(UUID.randomUUID(), "user@example.com", "User", null, "USER", false);
        JwtInformation original = new JwtInformation(userDto, "access-old", "refresh-old");
        JwtInformation replacement = new JwtInformation(userDto, "access-new", "refresh-new");

        registry.registerJwtInformation(original);
        registry.rotateJwtInformation("refresh-old", replacement);

        assertFalse(registry.hasActiveJwtInformationByAccessToken("access-old"));
        assertTrue(registry.hasActiveJwtInformationByAccessToken("access-new"));
        assertTrue(registry.hasActiveJwtInformationByRefreshToken("refresh-new"));
    }

    @Test
    void clearsExpiredJwtInformation() {
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        InMemoryJwtRegistry registry = new InMemoryJwtRegistry(2, tokenProvider);

        UserDto userDto = new UserDto(UUID.randomUUID(), "user@example.com", "User", null, "USER", false);
        JwtInformation info = new JwtInformation(userDto, "access", "refresh");
        registry.registerJwtInformation(info);

        when(tokenProvider.validateAccessToken("access")).thenReturn(false);
        when(tokenProvider.validateRefreshToken("refresh")).thenReturn(false);

        registry.clearExpiredJwtInformation();

        assertFalse(registry.hasActiveJwtInformationByAccessToken("access"));
        assertFalse(registry.hasActiveJwtInformationByRefreshToken("refresh"));
        assertFalse(registry.hasActiveJwtInformationByUserId(userDto.id()));
    }

    @Test
    void invalidatesJwtInformationByUserId() {
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        InMemoryJwtRegistry registry = new InMemoryJwtRegistry(2, tokenProvider);

        UserDto userDto = new UserDto(UUID.randomUUID(), "user@example.com", "User", null, "USER", false);
        JwtInformation info = new JwtInformation(userDto, "access", "refresh");
        registry.registerJwtInformation(info);

        assertTrue(registry.hasActiveJwtInformationByUserId(userDto.id()));

        registry.invalidateJwtInformationByUserId(userDto.id());

        assertFalse(registry.hasActiveJwtInformationByUserId(userDto.id()));
        assertFalse(registry.hasActiveJwtInformationByAccessToken("access"));
        assertFalse(registry.hasActiveJwtInformationByRefreshToken("refresh"));
    }
}
