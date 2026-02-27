package codeit.sb06.otboo.security.user;

import codeit.sb06.otboo.exception.user.LockedUserException;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TemporaryPasswordAuthenticationProvider implements AuthenticationProvider {

    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid email or password";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String rawPassword = authentication.getCredentials() == null
            ? ""
            : authentication.getCredentials().toString();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE));

        if (user.isLocked()) {
            throw new LockedUserException();
        }

        if (isMatched(user.getPassword(), rawPassword)) {
            return createAuthentication(user);
        }

        if (user.isTemporaryPasswordValidAt(LocalDateTime.now())
            && isMatched(user.getTemporaryPasswordHash(), rawPassword)) {
            user.clearTemporaryPassword();
            return createAuthentication(user);
        }

        throw new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private boolean isMatched(String encodedPassword, String rawPassword) {
        return encodedPassword != null && passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private Authentication createAuthentication(User user) {
        OtbooUserDetails userDetails = OtbooUserDetails.from(user);
        return new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
    }
}
