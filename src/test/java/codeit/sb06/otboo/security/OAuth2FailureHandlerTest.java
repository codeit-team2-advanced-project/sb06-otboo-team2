package codeit.sb06.otboo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import codeit.sb06.otboo.security.handler.OAuth2FailureHandler;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

class OAuth2FailureHandlerTest {

    @Test
    void redirectsToRootOnOAuth2Failure() throws Exception {
        OAuth2FailureHandler handler = new OAuth2FailureHandler();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        OAuth2AuthenticationException exception =
            new OAuth2AuthenticationException(new OAuth2Error("invalid", "bad", null));

        handler.onAuthenticationFailure(request, response, exception);

        assertEquals(302, response.getStatus());
        assertEquals("/", response.getRedirectedUrl());
    }
}
