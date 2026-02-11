package codeit.sb06.otboo.user.service;

import codeit.sb06.otboo.profile.service.ProfileServiceImpl;
import codeit.sb06.otboo.security.OtbooUserDetails;
import codeit.sb06.otboo.user.entity.Provider;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final ProfileServiceImpl profileService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = fetchUser(userRequest);
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = extractEmail(attributes);
        String name = extractName(attributes);
        String profileImageUrl = extractProfileImage(attributes);

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error("missing_email", "Kakao account email is missing", null)
            );
        }

        User user = userRepository.findByEmail(email)
            .map(existing -> {
                if (existing.getProvider() != null && existing.getProvider() != Provider.KAKAO) {
                    throw new OAuth2AuthenticationException(
                        new OAuth2Error("provider_mismatch", "Email already registered with another provider", null)
                    );
                }
                existing.updateOAuthProfile(name, profileImageUrl, Provider.KAKAO);
                return existing;
            })
            .orElseGet(() -> {
                User created = User.fromOAuth(email, name, profileImageUrl, Provider.KAKAO);
                User savedUser = userRepository.save(created);
                profileService.create(savedUser);
                return savedUser;
            });

        log.debug("Kakao OAuth2 login user resolved: {}", user.getEmail());
        return OtbooUserDetails.from(user, attributes);
    }

    protected OAuth2User fetchUser(OAuth2UserRequest userRequest) {
        return super.loadUser(userRequest);
    }

    private String extractEmail(Map<String, Object> attributes) {
        Map<String, Object> account = getMap(attributes, "kakao_account");
        if (account == null) {
            return null;
        }
        Object email = account.get("email");
        return email != null ? email.toString() : null;
    }

    private String extractName(Map<String, Object> attributes) {
        Map<String, Object> properties = getMap(attributes, "properties");
        if (properties != null && properties.get("nickname") != null) {
            return properties.get("nickname").toString();
        }
        Map<String, Object> account = getMap(attributes, "kakao_account");
        Map<String, Object> profile = account != null ? getMap(account, "profile") : null;
        if (profile != null && profile.get("nickname") != null) {
            return profile.get("nickname").toString();
        }
        return "kakao_user";
    }

    private String extractProfileImage(Map<String, Object> attributes) {
        Map<String, Object> properties = getMap(attributes, "properties");
        if (properties != null && properties.get("profile_image") != null) {
            return properties.get("profile_image").toString();
        }
        Map<String, Object> account = getMap(attributes, "kakao_account");
        Map<String, Object> profile = account != null ? getMap(account, "profile") : null;
        if (profile != null && profile.get("profile_image_url") != null) {
            return profile.get("profile_image_url").toString();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return null;
    }
}
