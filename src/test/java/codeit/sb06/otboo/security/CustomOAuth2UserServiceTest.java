package codeit.sb06.otboo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.profile.service.ProfileServiceImpl;
import codeit.sb06.otboo.security.user.OtbooUserDetails;
import codeit.sb06.otboo.user.entity.Provider;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.user.service.CustomOAuth2UserService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

class CustomOAuth2UserServiceTest {

    @Test
    void throwsWhenKakaoEmailMissing() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ProfileServiceImpl profileService = Mockito.mock(ProfileServiceImpl.class);

        CustomOAuth2UserService service = new CustomOAuth2UserService(userRepository, profileService) {
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

        assertThrows(OAuth2AuthenticationException.class, () -> service.loadUser(createUserRequest("kakao")));
    }

    @Test
    void throwsWhenKakaoProviderMismatch() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ProfileServiceImpl profileService = Mockito.mock(ProfileServiceImpl.class);

        User existing = User.fromOAuth("user@example.com", "tester", null, Provider.LOCAL);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(existing));

        CustomOAuth2UserService service = new CustomOAuth2UserService(userRepository, profileService) {
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

        assertThrows(OAuth2AuthenticationException.class, () -> service.loadUser(createUserRequest("kakao")));
    }

    @Test
    void updatesExistingKakaoUserAndReturnsUserDetails() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ProfileServiceImpl profileService = Mockito.mock(ProfileServiceImpl.class);

        User existing = User.fromOAuth("user@example.com", "old-name", "old-image", Provider.KAKAO);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(existing));

        CustomOAuth2UserService service = new CustomOAuth2UserService(userRepository, profileService) {
            @Override
            protected OAuth2User fetchUser(OAuth2UserRequest userRequest) {
                Map<String, Object> attributes = Map.of(
                    "id", 12345L,
                    "properties", Map.of(
                        "nickname", "new-name",
                        "profile_image", "https://img.example/new.png"
                    ),
                    "kakao_account", Map.of("email", "user@example.com")
                );
                return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "id"
                );
            }
        };

        OAuth2User result = service.loadUser(createUserRequest("kakao"));

        OtbooUserDetails userDetails = assertInstanceOf(OtbooUserDetails.class, result);
        assertEquals("user@example.com", userDetails.getUserDto().email());
        assertEquals("new-name", existing.getName());
        assertEquals("https://img.example/new.png", existing.getProfileImageUrl());
        assertEquals(Provider.KAKAO, existing.getProvider());
        verify(userRepository, never()).save(any(User.class));
        verify(profileService, never()).create(any(User.class));
    }

    @Test
    void createsNewKakaoUserAndProfileWhenEmailNotFound() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ProfileServiceImpl profileService = Mockito.mock(ProfileServiceImpl.class);
        when(userRepository.findByEmail("new-user@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomOAuth2UserService service = new CustomOAuth2UserService(userRepository, profileService) {
            @Override
            protected OAuth2User fetchUser(OAuth2UserRequest userRequest) {
                Map<String, Object> attributes = Map.of(
                    "id", 12345L,
                    "kakao_account", Map.of(
                        "email", "new-user@example.com",
                        "profile", Map.of(
                            "nickname", "new-user",
                            "profile_image_url", "https://img.example/profile-url.png"
                        )
                    )
                );
                return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "id"
                );
            }
        };

        OAuth2User result = service.loadUser(createUserRequest("kakao"));

        OtbooUserDetails userDetails = assertInstanceOf(OtbooUserDetails.class, result);
        assertEquals("new-user@example.com", userDetails.getUserDto().email());
        verify(userRepository).save(any(User.class));
        verify(profileService).create(any(User.class));
    }

    @Test
    void throwsWhenGoogleEmailMissing() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ProfileServiceImpl profileService = Mockito.mock(ProfileServiceImpl.class);

        CustomOAuth2UserService service = new CustomOAuth2UserService(userRepository, profileService) {
            @Override
            protected OAuth2User fetchUser(OAuth2UserRequest userRequest) {
                Map<String, Object> attributes = Map.of("name", "google-user");
                return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "name"
                );
            }
        };

        assertThrows(OAuth2AuthenticationException.class, () -> service.loadUser(createUserRequest("google")));
    }

    @Test
    void throwsWhenGoogleProviderMismatch() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ProfileServiceImpl profileService = Mockito.mock(ProfileServiceImpl.class);

        User existing = User.fromOAuth("google@example.com", "tester", null, Provider.KAKAO);
        when(userRepository.findByEmail("google@example.com")).thenReturn(Optional.of(existing));

        CustomOAuth2UserService service = new CustomOAuth2UserService(userRepository, profileService) {
            @Override
            protected OAuth2User fetchUser(OAuth2UserRequest userRequest) {
                Map<String, Object> attributes = Map.of(
                    "email", "google@example.com",
                    "name", "google-user",
                    "picture", "https://img.example/google.png"
                );
                return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "email"
                );
            }
        };

        assertThrows(OAuth2AuthenticationException.class, () -> service.loadUser(createUserRequest("google")));
    }

    @Test
    void updatesExistingGoogleUserAndReturnsUserDetails() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ProfileServiceImpl profileService = Mockito.mock(ProfileServiceImpl.class);

        User existing = User.fromOAuth("google@example.com", "old-name", "old-image", Provider.GOOGLE);
        when(userRepository.findByEmail("google@example.com")).thenReturn(Optional.of(existing));

        CustomOAuth2UserService service = new CustomOAuth2UserService(userRepository, profileService) {
            @Override
            protected OAuth2User fetchUser(OAuth2UserRequest userRequest) {
                Map<String, Object> attributes = Map.of(
                    "email", "google@example.com",
                    "name", "new-google-name",
                    "picture", "https://img.example/google-new.png"
                );
                return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "email"
                );
            }
        };

        OAuth2User result = service.loadUser(createUserRequest("google"));

        OtbooUserDetails userDetails = assertInstanceOf(OtbooUserDetails.class, result);
        assertEquals("google@example.com", userDetails.getUserDto().email());
        assertEquals("new-google-name", existing.getName());
        assertEquals("https://img.example/google-new.png", existing.getProfileImageUrl());
        assertEquals(Provider.GOOGLE, existing.getProvider());
        verify(userRepository, never()).save(any(User.class));
        verify(profileService, never()).create(any(User.class));
    }

    @Test
    void createsNewGoogleUserAndProfileWhenEmailNotFound() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ProfileServiceImpl profileService = Mockito.mock(ProfileServiceImpl.class);
        when(userRepository.findByEmail("new-google@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomOAuth2UserService service = new CustomOAuth2UserService(userRepository, profileService) {
            @Override
            protected OAuth2User fetchUser(OAuth2UserRequest userRequest) {
                Map<String, Object> attributes = Map.of(
                    "email", "new-google@example.com",
                    "name", "new-google-user",
                    "picture", "https://img.example/google-profile.png"
                );
                return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "email"
                );
            }
        };

        OAuth2User result = service.loadUser(createUserRequest("google"));

        OtbooUserDetails userDetails = assertInstanceOf(OtbooUserDetails.class, result);
        assertEquals("new-google@example.com", userDetails.getUserDto().email());
        verify(userRepository).save(any(User.class));
        verify(profileService).create(any(User.class));
    }

    private OAuth2UserRequest createUserRequest(String registrationId) {
        OAuth2UserRequest userRequest = Mockito.mock(OAuth2UserRequest.class);
        ClientRegistration registration = ClientRegistration.withRegistrationId(registrationId)
            .clientId("client-id")
            .clientSecret("client-secret")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .authorizationUri("https://example.com/oauth/authorize")
            .tokenUri("https://example.com/oauth/token")
            .userInfoUri("https://example.com/userinfo")
            .userNameAttributeName("id")
            .build();
        when(userRequest.getClientRegistration()).thenReturn(registration);
        return userRequest;
    }
}
