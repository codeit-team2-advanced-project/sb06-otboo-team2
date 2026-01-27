package codeit.sb06.otboo.user.controller;

import codeit.sb06.otboo.security.dto.JwtDto;
import codeit.sb06.otboo.user.service.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(@CookieValue(name = "REFRESH_TOKEN", required = true) String refreshToken) {
        log.debug("Refresh token requested");
        log.trace("Refresh Token: {}", refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(authServiceImpl.refreshToken(refreshToken));
    }

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken){
        log.debug("CSRF token requested");
        log.trace("CSRF Token: {}", csrfToken.getToken());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
