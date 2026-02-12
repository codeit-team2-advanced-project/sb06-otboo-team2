package codeit.sb06.otboo.security;

import codeit.sb06.otboo.user.dto.UserDto;
import java.util.Map;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;

public class OtbooOidcUserDetails extends OtbooUserDetails implements OidcUser {

    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;

    public OtbooOidcUserDetails(
        UserDto userDto,
        String password,
        Map<String, Object> attributes,
        OidcIdToken idToken,
        OidcUserInfo userInfo
    ) {
        super(userDto, password, attributes);
        this.idToken = idToken;
        this.userInfo = userInfo;
    }

    public static OtbooOidcUserDetails from(OtbooUserDetails userDetails, OidcUser oidcUser) {
        return new OtbooOidcUserDetails(
            userDetails.getUserDto(),
            userDetails.getPassword(),
            userDetails.getAttributes(),
            oidcUser.getIdToken(),
            oidcUser.getUserInfo()
        );
    }

    @Override
    public Map<String, Object> getClaims() {
        return getAttributes();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }
}
