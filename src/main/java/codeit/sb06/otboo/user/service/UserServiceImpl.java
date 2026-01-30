package codeit.sb06.otboo.user.service;

import codeit.sb06.otboo.exception.user.UserAlreadyExistException;
import codeit.sb06.otboo.exception.user.UserNotFoundException;
import codeit.sb06.otboo.profile.service.ProfileServiceImpl;
import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.user.dto.request.UserCreateRequest;
import codeit.sb06.otboo.user.dto.request.UserSliceRequest;
import codeit.sb06.otboo.user.dto.response.UserDtoCursorResponse;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileServiceImpl profileService;

    public UserDto create(UserCreateRequest userCreateRequest) {
        log.debug("Create user start: {}", userCreateRequest);

        String email = userCreateRequest.email();

        if(userRepository.findByEmail(email).isPresent()){
            throw new UserAlreadyExistException();
        }

        User user = User.from(userCreateRequest);
        user.setEncryptPassword(passwordEncoder, userCreateRequest.password());

        User savedUser = userRepository.save(user);
        profileService.create(savedUser);
        return UserDto.from(savedUser);
    }

    public UserDtoCursorResponse getUsersCursor(UserSliceRequest userSliceRequest){
        log.debug("Get users cursor start: {}", userSliceRequest);

        Slice<User> response = userRepository.findUsersBySlice(userSliceRequest);
        return UserDtoCursorResponse.from(response, userSliceRequest);
    }

    public UserDto changeUserRole(UUID userId, String userRole){
        log.debug("Change user role start: {}, {}", userId, userRole);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        user.changeRole(Role.valueOf(userRole));
        User updatedUser = userRepository.save(user);
        return UserDto.from(updatedUser);
    }

    public UserDto changeLockStatus(UUID userId, boolean lockStatus){
        log.debug("Change user lock status start: {}, {}", userId, lockStatus);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        user.changeLockStatus(lockStatus);
        User updatedUser = userRepository.save(user);
        return UserDto.from(updatedUser);
    }

}
