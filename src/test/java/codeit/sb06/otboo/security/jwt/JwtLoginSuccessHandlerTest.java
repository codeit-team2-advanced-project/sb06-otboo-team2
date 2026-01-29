package codeit.sb06.otboo.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.security.dto.JwtInformation;
import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.security.OtbooUserDetails;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

class JwtLoginSuccessHandlerTest {

    @Test
    void writesJwtResponseAndRegistersInformation() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JwtTokenProvider tokenProvider = Mockito.mock(JwtTokenProvider.class);
        JwtRegistry jwtRegistry = Mockito.mock(JwtRegistry.class);
        JwtLoginSuccessHandler handler = new JwtLoginSuccessHandler(objectMapper, tokenProvider, jwtRegistry);

        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "user@example.com",
            "User",
            null,
            "USER",
            false
        );
        OtbooUserDetails userDetails = new OtbooUserDetails(userDto, "password");
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
        ObjectMapper objectMapper = new ObjectMapper();
        JwtTokenProvider tokenProvider = Mockito.mock(JwtTokenProvider.class);
        JwtRegistry jwtRegistry = Mockito.mock(JwtRegistry.class);
        JwtLoginSuccessHandler handler = new JwtLoginSuccessHandler(objectMapper, tokenProvider, jwtRegistry);

        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "user@example.com",
            "User",
            null,
            "USER",
            false
        );
        OtbooUserDetails userDetails = new OtbooUserDetails(userDto, "password");
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
