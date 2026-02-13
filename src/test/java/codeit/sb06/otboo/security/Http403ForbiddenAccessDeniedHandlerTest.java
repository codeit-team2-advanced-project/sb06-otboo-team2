package codeit.sb06.otboo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import codeit.sb06.otboo.security.handler.Http403ForbiddenAccessDeniedHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

class Http403ForbiddenAccessDeniedHandlerTest {

    @Test
    void writesForbiddenResponse() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Http403ForbiddenAccessDeniedHandler handler = new Http403ForbiddenAccessDeniedHandler(objectMapper);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("denied"));

        assertEquals(403, response.getStatus());
        assertTrue(response.getContentType().startsWith("application/json"));

        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertEquals("AccessDeniedException", body.get("exceptionName").asText());
        assertEquals("denied", body.get("message").asText());
    }
}
