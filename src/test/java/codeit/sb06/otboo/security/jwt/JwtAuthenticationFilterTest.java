package codeit.sb06.otboo.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.security.OtbooUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import jakarta.servlet.FilterChain;

class JwtAuthenticationFilterTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void setsAuthenticationWhenTokenIsValid() throws Exception {
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        JwtRegistry jwtRegistry = mock(JwtRegistry.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
            tokenProvider,
            userDetailsService,
            objectMapper,
            jwtRegistry
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-value");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "user@example.com",
            "User",
            null,
            "USER",
            false
        );
        UserDetails userDetails = new OtbooUserDetails(userDto, "password");

        when(tokenProvider.validateAccessToken("token-value")).thenReturn(true);
        when(jwtRegistry.hasActiveJwtInformationByAccessToken("token-value")).thenReturn(true);
        when(tokenProvider.getUserNameFromToken("token-value")).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);

        filter.doFilter(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user@example.com", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(chain).doFilter(request, response);
    }

    @Test
    void rejectsRequestWhenTokenIsInvalid() throws Exception {
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        JwtRegistry jwtRegistry = mock(JwtRegistry.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
            tokenProvider,
            userDetailsService,
            objectMapper,
            jwtRegistry
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-value");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(tokenProvider.validateAccessToken("token-value")).thenReturn(false);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(401, response.getStatus());
        verifyNoInteractions(chain);
    }

    @Test
    void continuesWhenNoAuthorizationHeader() throws Exception {
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        JwtRegistry jwtRegistry = mock(JwtRegistry.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
            tokenProvider,
            userDetailsService,
            objectMapper,
            jwtRegistry
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
        verifyNoMoreInteractions(tokenProvider, jwtRegistry, userDetailsService);
    }
}
