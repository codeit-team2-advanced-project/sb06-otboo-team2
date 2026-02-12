package codeit.sb06.otboo.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.security.OtbooOidcUserDetails;
import codeit.sb06.otboo.security.OtbooUserDetails;
import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.user.entity.Provider;
import codeit.sb06.otboo.user.entity.Role;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

class CustomOidcUserServiceTest {

    @Test
    void convertsOidcUserToOtbooOidcUserDetails() {
        CustomOAuth2UserService customOAuth2UserService = Mockito.mock(CustomOAuth2UserService.class);

        Map<String, Object> attributes = Map.of(
            "sub", "google-sub",
            "email", "google@example.com",
            "name", "google-user",
            "picture", "https://img.example/google.png"
        );
        OidcIdToken idToken = new OidcIdToken(
            "id-token",
            Instant.now(),
            Instant.now().plusSeconds(300),
            attributes
        );
        OidcUserInfo userInfo = new OidcUserInfo(attributes);
        OidcUser oidcUser = new DefaultOidcUser(
            List.of(new SimpleGrantedAuthority("ROLE_USER")),
            idToken,
            userInfo,
            "sub"
        );

        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "google@example.com",
            LocalDateTime.of(2026, 1, 1, 0, 0),
            Role.USER.name(),
            false
        );
        OtbooUserDetails otbooUserDetails = new OtbooUserDetails(userDto, "password", attributes);
        when(customOAuth2UserService.resolveUserDetails(attributes, Provider.GOOGLE))
            .thenReturn(otbooUserDetails);

        CustomOidcUserService service = new CustomOidcUserService(customOAuth2UserService) {
            @Override
            protected OidcUser fetchOidcUser(OidcUserRequest userRequest) {
                return oidcUser;
            }
        };

        OidcUserRequest userRequest = Mockito.mock(OidcUserRequest.class);
        OidcUser result = service.loadUser(userRequest);

        OtbooOidcUserDetails principal = assertInstanceOf(OtbooOidcUserDetails.class, result);
        assertEquals("google@example.com", principal.getUserDto().email());
        assertEquals("id-token", principal.getIdToken().getTokenValue());

        verify(customOAuth2UserService).resolveUserDetails(attributes, Provider.GOOGLE);
    }
}
