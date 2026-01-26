package codeit.sb06.otboo.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.Cookie;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class JwtLogoutHandlerTest {

    @Test
    void skipsRegistryWhenNoCookiesPresent() {
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        JwtRegistry jwtRegistry = mock(JwtRegistry.class);
        JwtLogoutHandler handler = new JwtLogoutHandler(tokenProvider, jwtRegistry);

        Cookie expirationCookie = new Cookie("refreshToken", "");
        expirationCookie.setMaxAge(0);
        when(tokenProvider.generateRefreshTokenExpirationCookie()).thenReturn(expirationCookie);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.logout(request, response, null);

        assertNotNull(response.getCookie("refreshToken"));
        verifyNoInteractions(jwtRegistry);
    }

    @Test
    void invalidatesJwtInformationWhenRefreshTokenPresent() {
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        JwtRegistry jwtRegistry = mock(JwtRegistry.class);
        JwtLogoutHandler handler = new JwtLogoutHandler(tokenProvider, jwtRegistry);

        Cookie expirationCookie = new Cookie("refreshToken", "");
        expirationCookie.setMaxAge(0);
        when(tokenProvider.generateRefreshTokenExpirationCookie()).thenReturn(expirationCookie);
        when(tokenProvider.getUserId("refresh-token")).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("refreshToken", "refresh-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.logout(request, response, null);

        assertNotNull(response.getCookie("refreshToken"));
        assertEquals(0, response.getCookie("refreshToken").getMaxAge());
        verify(jwtRegistry).invalidateJwtInformationByUserId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
    }
}
