package codeit.sb06.otboo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import codeit.sb06.otboo.security.handler.OAuth2FailureHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

class OAuth2FailureHandlerTest {

    @Test
    void writesErrorResponse() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        OAuth2FailureHandler handler = new OAuth2FailureHandler(objectMapper);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        OAuth2AuthenticationException exception =
            new OAuth2AuthenticationException(new OAuth2Error("invalid", "bad", null));

        handler.onAuthenticationFailure(request, response, exception);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentType().startsWith("application/json"));

        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertEquals("OAuth2AuthenticationException", body.get("exceptionName").asText());
        assertTrue(body.get("message").asText() != null);
    }
}
