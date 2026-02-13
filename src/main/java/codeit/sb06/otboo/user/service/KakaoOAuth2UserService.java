package codeit.sb06.otboo.user.service;

import codeit.sb06.otboo.profile.service.ProfileServiceImpl;
import codeit.sb06.otboo.user.entity.Provider;
import codeit.sb06.otboo.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class KakaoOAuth2UserService extends CustomOAuth2UserService {

    public KakaoOAuth2UserService(UserRepository userRepository, ProfileServiceImpl profileService) {
        super(userRepository, profileService);
    }

    @Override
    protected Provider resolveProvider(String registrationId) {
        return Provider.KAKAO;
    }
}
