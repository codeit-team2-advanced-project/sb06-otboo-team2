package codeit.sb06.otboo.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.profile.service.ProfileServiceImpl;
import codeit.sb06.otboo.user.entity.Provider;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.user.service.KakaoOAuth2UserService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

class KakaoOAuth2UserServiceTest {

    @Test
    void throwsWhenEmailMissing() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ProfileServiceImpl profileService = Mockito.mock(ProfileServiceImpl.class);

        KakaoOAuth2UserService service = new KakaoOAuth2UserService(userRepository, profileService) {
            @Override
            protected OAuth2User fetchUser(OAuth2UserRequest userRequest) {
                Map<String, Object> attributes = Map.of(
                    "id", 12345L,
                    "kakao_account", Map.of("profile", Map.of("nickname", "tester"))
                );
                return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "id"
                );
            }
        };

        assertThrows(OAuth2AuthenticationException.class, () -> service.loadUser(Mockito.mock(OAuth2UserRequest.class)));
    }

    @Test
    void throwsWhenProviderMismatch() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ProfileServiceImpl profileService = Mockito.mock(ProfileServiceImpl.class);

        User existing = User.fromOAuth("user@example.com", "tester", null, Provider.LOCAL);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(existing));

        KakaoOAuth2UserService service = new KakaoOAuth2UserService(userRepository, profileService) {
            @Override
            protected OAuth2User fetchUser(OAuth2UserRequest userRequest) {
                Map<String, Object> attributes = Map.of(
                    "id", 12345L,
                    "kakao_account", Map.of("email", "user@example.com")
                );
                return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "id"
                );
            }
        };

        assertThrows(OAuth2AuthenticationException.class, () -> service.loadUser(Mockito.mock(OAuth2UserRequest.class)));
    }
}
