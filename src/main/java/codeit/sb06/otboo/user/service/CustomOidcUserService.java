package codeit.sb06.otboo.user.service;

import codeit.sb06.otboo.security.OtbooOidcUserDetails;
import codeit.sb06.otboo.security.OtbooUserDetails;
import codeit.sb06.otboo.user.entity.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = fetchOidcUser(userRequest);
        OtbooUserDetails userDetails =
            customOAuth2UserService.resolveUserDetails(oidcUser.getAttributes(), Provider.GOOGLE);
        return OtbooOidcUserDetails.from(userDetails, oidcUser);
    }

    protected OidcUser fetchOidcUser(OidcUserRequest userRequest) {
        return super.loadUser(userRequest);
    }
}
