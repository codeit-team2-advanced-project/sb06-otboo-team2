package codeit.sb06.otboo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;

class SpaCsrfTokenRequestHandlerTest {

    @Test
    void resolvesTokenFromHeaderWhenPresent() {
        SpaCsrfTokenRequestHandler handler = new SpaCsrfTokenRequestHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CsrfToken token = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "token-value");

        request.addHeader("X-CSRF-TOKEN", "header-token");

        String resolved = handler.resolveCsrfTokenValue(request, token);

        assertEquals("header-token", resolved);
    }

    @Test
    void returnsNullWhenNoHeaderOrParameter() {
        SpaCsrfTokenRequestHandler handler = new SpaCsrfTokenRequestHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        CsrfToken token = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "token-value");

        String resolved = handler.resolveCsrfTokenValue(request, token);

        assertNull(resolved);
    }

    @Test
    void handlePopulatesRequestAttributes() {
        SpaCsrfTokenRequestHandler handler = new SpaCsrfTokenRequestHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CsrfToken token = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "token-value");

        handler.handle(request, response, () -> token);

        Object byClass = request.getAttribute(CsrfToken.class.getName());
        Object byParameter = request.getAttribute(token.getParameterName());
        assertTrue(byClass != null || byParameter != null);
    }
}
