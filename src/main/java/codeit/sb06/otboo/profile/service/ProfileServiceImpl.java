package codeit.sb06.otboo.profile.service;

import codeit.sb06.otboo.exception.user.UserNotFoundException;
import codeit.sb06.otboo.profile.dto.ProfileDto;
import codeit.sb06.otboo.profile.entity.Profile;
import codeit.sb06.otboo.profile.repository.ProfileRepository;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileServiceImpl {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileDto create(User user){
        log.debug("create profile start: {}", user.getId());
        if(userRepository.findByEmail(user.getEmail()).isEmpty()){
            throw new UserNotFoundException("user not found");
        }

        Profile profile = Profile.from(user);
        Profile savedProfile = profileRepository.save(profile);
        return ProfileDto.from(savedProfile);
    }
}
