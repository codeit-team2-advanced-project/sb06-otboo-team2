package codeit.sb06.otboo.config;

import codeit.sb06.otboo.profile.service.ProfileServiceImpl;
import codeit.sb06.otboo.user.dto.request.UserCreateRequest;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private static final String ADMIN_EMAIL = "admin@admin.com";
    private static final String ADMIN_PASSWORD = "admin1!";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileServiceImpl profileService;

    @Override
    public void run(String... args) {
        User admin = userRepository.findByEmail(ADMIN_EMAIL)
            .orElseGet(() -> {
                User newAdmin = User.from(new UserCreateRequest("admin", ADMIN_EMAIL, ADMIN_PASSWORD));
                newAdmin.changeRole(Role.ADMIN);
                newAdmin.changeLockStatus(false);
                newAdmin.setEncryptPassword(passwordEncoder, ADMIN_PASSWORD);

                userRepository.save(newAdmin);
                profileService.create(newAdmin);

                return newAdmin;
            });
        log.info("Admin account initialized: {}", ADMIN_EMAIL);
    }
}
