package codeit.sb06.otboo.user.service;

import codeit.sb06.otboo.exception.RootException;
import codeit.sb06.otboo.exception.auth.InvalidTokenException;
import codeit.sb06.otboo.exception.auth.InvalidUserDetailException;
import codeit.sb06.otboo.security.OtbooUserDetails;
import codeit.sb06.otboo.security.dto.JwtDto;
import codeit.sb06.otboo.security.dto.JwtInformation;
import codeit.sb06.otboo.security.jwt.JwtRegistry;
import codeit.sb06.otboo.security.jwt.JwtTokenProvider;
import codeit.sb06.otboo.user.repository.UsersRepository;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final UsersRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final UserDetailsService userDetailsService;

    public JwtInformation refreshToken(String refreshToken) {
        if(!jwtTokenProvider.validateRefreshToken(refreshToken)
            || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        String userEmail = jwtTokenProvider.getUserNameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if(!(userDetails instanceof OtbooUserDetails otbooUserDetails)){
            throw new InvalidUserDetailException("Invalid user details");
        }

        try{
            String newAccessToken = jwtTokenProvider.generateAccessToken(otbooUserDetails);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(otbooUserDetails);
            log.info("Access token refreshed for user: {}", userEmail);

            JwtInformation newJwtInformation = new JwtInformation(
                otbooUserDetails.getUserDto(),
                newAccessToken,
                newRefreshToken
            );

            jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);

            return newJwtInformation;
        } catch (JOSEException e){
            log.error("failed to generate new JWT tokens for user: {}", userEmail, e);
            throw new RootException("Failed to generate new tokens", e);
        }

    }
}
