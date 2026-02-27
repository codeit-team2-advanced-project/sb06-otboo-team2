package codeit.sb06.otboo.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
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
                Map<String, Object> attributes = Map.of("id", 12345L);
                return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "id"
                );
            }
        };

        assertThrows(OAuth2AuthenticationException.class, () -> service.loadUser(createUserRequest()));
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

        assertThrows(OAuth2AuthenticationException.class, () -> service.loadUser(createUserRequest()));
    }

    @Test
    void updatesExistingUserFromProperties() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ProfileServiceImpl profileService = Mockito.mock(ProfileServiceImpl.class);

        User existing = User.fromOAuth("user@example.com", "old-name", "old-image", Provider.KAKAO);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(existing));

        KakaoOAuth2UserService service = new KakaoOAuth2UserService(userRepository, profileService) {
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

        OAuth2User result = service.loadUser(createUserRequest());

        OtbooUserDetails userDetails = assertInstanceOf(OtbooUserDetails.class, result);
        assertEquals("user@example.com", userDetails.getUserDto().email());
        assertEquals("new-name", existing.getName());
        assertEquals("https://img.example/new.png", existing.getProfileImageUrl());
        assertEquals(Provider.KAKAO, existing.getProvider());
        verify(userRepository, never()).save(any(User.class));
        verify(profileService, never()).create(any(User.class));
    }

    @Test
    void updatesExistingUserFromAccountProfileFallback() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ProfileServiceImpl profileService = Mockito.mock(ProfileServiceImpl.class);

        User existing = User.fromOAuth("user@example.com", "old-name", "old-image", Provider.KAKAO);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(existing));

        KakaoOAuth2UserService service = new KakaoOAuth2UserService(userRepository, profileService) {
            @Override
            protected OAuth2User fetchUser(OAuth2UserRequest userRequest) {
                Map<String, Object> attributes = Map.of(
                    "id", 12345L,
                    "kakao_account", Map.of(
                        "email", "user@example.com",
                        "profile", Map.of(
                            "nickname", "fallback-name",
                            "profile_image_url", "https://img.example/fallback.png"
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

        OAuth2User result = service.loadUser(createUserRequest());

        OtbooUserDetails userDetails = assertInstanceOf(OtbooUserDetails.class, result);
        assertEquals("user@example.com", userDetails.getUserDto().email());
        assertEquals("fallback-name", existing.getName());
        assertEquals("https://img.example/fallback.png", existing.getProfileImageUrl());
        verify(userRepository, never()).save(any(User.class));
        verify(profileService, never()).create(any(User.class));
    }

    @Test
    void createsUserWithDefaultNameWhenNicknameMissing() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ProfileServiceImpl profileService = Mockito.mock(ProfileServiceImpl.class);

        when(userRepository.findByEmail("new-user@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        KakaoOAuth2UserService service = new KakaoOAuth2UserService(userRepository, profileService) {
            @Override
            protected OAuth2User fetchUser(OAuth2UserRequest userRequest) {
                Map<String, Object> attributes = Map.of(
                    "id", 12345L,
                    "properties", "not-a-map",
                    "kakao_account", Map.of("email", "new-user@example.com")
                );
                return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "id"
                );
            }
        };

        OAuth2User result = service.loadUser(createUserRequest());

        OtbooUserDetails userDetails = assertInstanceOf(OtbooUserDetails.class, result);
        assertEquals("new-user@example.com", userDetails.getUserDto().email());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertEquals("kakao_user", savedUser.getName());
        assertNull(savedUser.getProfileImageUrl());
        assertEquals(Provider.KAKAO, savedUser.getProvider());
        verify(profileService).create(savedUser);
    }

    private OAuth2UserRequest createUserRequest() {
        OAuth2UserRequest userRequest = Mockito.mock(OAuth2UserRequest.class);
        ClientRegistration registration = ClientRegistration.withRegistrationId("kakao")
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
