package codeit.sb06.otboo.security;

import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.user.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@EqualsAndHashCode(of = "userDto")
@Getter
@RequiredArgsConstructor
public class OtbooUserDetails implements UserDetails, OAuth2User {

    private final UserDto userDto;
    private final String password;
    private final Map<String, Object> attributes;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userDto.role()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userDto.email();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return userDto.id().toString();
    }

    public static OtbooUserDetails from(User user) {
        return new OtbooUserDetails(UserDto.from(user), user.getPassword(), Map.of());
    }

    public static OtbooUserDetails from(User user, Map<String, Object> attributes) {
        return new OtbooUserDetails(UserDto.from(user), user.getPassword(), attributes);
    }
}
