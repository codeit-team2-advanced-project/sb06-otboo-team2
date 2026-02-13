package codeit.sb06.otboo.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.exception.auth.InvalidTokenException;
import codeit.sb06.otboo.exception.auth.InvalidUserDetailException;
import codeit.sb06.otboo.exception.RootException;
import codeit.sb06.otboo.security.OtbooUserDetails;
import codeit.sb06.otboo.security.dto.JwtInformation;
import codeit.sb06.otboo.security.jwt.JwtRegistry;
import codeit.sb06.otboo.security.jwt.JwtTokenProvider;
import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.user.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

class AuthServiceImplTest {

    @Test
    void refreshTokenRotatesAndReturnsNewInformation() throws Exception {
        UserRepository userRepository = mock(UserRepository.class);
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        JwtRegistry jwtRegistry = mock(JwtRegistry.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);

        AuthServiceImpl service = new AuthServiceImpl(
            userRepository,
            tokenProvider,
            jwtRegistry,
            userDetailsService
        );

        String refreshToken = "refresh-old";
        when(tokenProvider.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getUserNameFromToken(refreshToken)).thenReturn("user@example.com");

        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "user@example.com",
            LocalDateTime.of(2026, 1, 1, 0, 0),
            "USER",
            false
        );
        OtbooUserDetails userDetails = new OtbooUserDetails(userDto, "password", Map.of());
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);

        when(tokenProvider.generateAccessToken(eq(userDetails))).thenReturn("access-new");
        when(tokenProvider.generateRefreshToken(eq(userDetails))).thenReturn("refresh-new");

        JwtInformation info = service.refreshToken(refreshToken);

        assertEquals("access-new", info.getAccessToken());
        assertEquals("refresh-new", info.getRefreshToken());
        assertEquals(userDto, info.getUserDto());

        verify(jwtRegistry).rotateJwtInformation(eq(refreshToken), any(JwtInformation.class));
    }

    @Test
    void refreshTokenThrowsWhenTokenInvalid() {
        UserRepository userRepository = mock(UserRepository.class);
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        JwtRegistry jwtRegistry = mock(JwtRegistry.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);

        AuthServiceImpl service = new AuthServiceImpl(
            userRepository,
            tokenProvider,
            jwtRegistry,
            userDetailsService
        );

        String refreshToken = "refresh-old";
        when(tokenProvider.validateRefreshToken(refreshToken)).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> service.refreshToken(refreshToken));
    }

    @Test
    void refreshTokenThrowsWhenUserDetailsInvalid() {
        UserRepository userRepository = mock(UserRepository.class);
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        JwtRegistry jwtRegistry = mock(JwtRegistry.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);

        AuthServiceImpl service = new AuthServiceImpl(
            userRepository,
            tokenProvider,
            jwtRegistry,
            userDetailsService
        );

        String refreshToken = "refresh-old";
        when(tokenProvider.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getUserNameFromToken(refreshToken)).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com"))
            .thenReturn(mock(UserDetails.class));

        assertThrows(InvalidUserDetailException.class, () -> service.refreshToken(refreshToken));
    }

    @Test
    void refreshTokenThrowsWhenTokenGenerationFails() throws Exception {
        UserRepository userRepository = mock(UserRepository.class);
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        JwtRegistry jwtRegistry = mock(JwtRegistry.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);

        AuthServiceImpl service = new AuthServiceImpl(
            userRepository,
            tokenProvider,
            jwtRegistry,
            userDetailsService
        );

        String refreshToken = "refresh-old";
        when(tokenProvider.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getUserNameFromToken(refreshToken)).thenReturn("user@example.com");

        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "user@example.com",
            LocalDateTime.of(2026, 1, 1, 0, 0),
            "USER",
            false
        );
        OtbooUserDetails userDetails = new OtbooUserDetails(userDto, "password", Map.of());
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(tokenProvider.generateAccessToken(eq(userDetails)))
            .thenThrow(new JOSEException("fail"));

        RootException ex = assertThrows(RootException.class, () -> service.refreshToken(refreshToken));
        assertEquals(401, ex.getStatus());
        verify(jwtRegistry, never()).rotateJwtInformation(eq(refreshToken), any(JwtInformation.class));
    }
}
