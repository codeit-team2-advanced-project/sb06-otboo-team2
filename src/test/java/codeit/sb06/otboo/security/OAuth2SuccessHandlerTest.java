package codeit.sb06.otboo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.security.dto.JwtInformation;
import codeit.sb06.otboo.security.jwt.JwtRegistry;
import codeit.sb06.otboo.security.jwt.JwtTokenProvider;
import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.user.entity.Role;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

class OAuth2SuccessHandlerTest {

    @Test
    void writesJwtResponseAndRegistersInformation() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        JwtTokenProvider tokenProvider = Mockito.mock(JwtTokenProvider.class);
        JwtRegistry jwtRegistry = Mockito.mock(JwtRegistry.class);
        OAuth2SuccessHandler handler = new OAuth2SuccessHandler(tokenProvider, jwtRegistry, objectMapper);

        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "user@example.com",
            LocalDateTime.now(),
            Role.USER.name(),
            false
        );
        OtbooUserDetails userDetails = new OtbooUserDetails(userDto, "password", Map.of());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);

        when(tokenProvider.generateAccessToken(eq(userDetails))).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken(eq(userDetails))).thenReturn("refresh-token");
        when(tokenProvider.generateRefreshTokenCookie(eq("refresh-token")))
            .thenReturn(new Cookie("refreshToken", "refresh-token"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication);

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));
        assertNotNull(response.getCookie("refreshToken"));

        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertEquals("access-token", body.get("accessToken").asText());
        assertEquals("user@example.com", body.get("userDto").get("email").asText());

        ArgumentCaptor<JwtInformation> captor = ArgumentCaptor.forClass(JwtInformation.class);
        verify(jwtRegistry).registerJwtInformation(captor.capture());
        JwtInformation info = captor.getValue();
        assertEquals(userDto, info.getUserDto());
        assertEquals("access-token", info.getAccessToken());
        assertEquals("refresh-token", info.getRefreshToken());
    }

    @Test
    void writesErrorResponseWhenTokenGenerationFails() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        JwtTokenProvider tokenProvider = Mockito.mock(JwtTokenProvider.class);
        JwtRegistry jwtRegistry = Mockito.mock(JwtRegistry.class);
        OAuth2SuccessHandler handler = new OAuth2SuccessHandler(tokenProvider, jwtRegistry, objectMapper);

        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "user@example.com",
            LocalDateTime.now(),
            Role.USER.name(),
            false
        );
        OtbooUserDetails userDetails = new OtbooUserDetails(userDto, "password", Map.of());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);

        when(tokenProvider.generateAccessToken(eq(userDetails)))
            .thenThrow(new JOSEException("signing failed"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication);

        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertEquals("RuntimeException", body.get("exceptionName").asText());
        assertEquals("Failed to generate JWT token", body.get("message").asText());
    }
}
