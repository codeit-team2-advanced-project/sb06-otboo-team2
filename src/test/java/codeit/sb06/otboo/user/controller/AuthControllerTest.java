package codeit.sb06.otboo.user.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import codeit.sb06.otboo.config.WebMvcConfig;
import codeit.sb06.otboo.security.resolver.CurrentUserIdArgumentResolver;
import codeit.sb06.otboo.security.dto.JwtInformation;
import codeit.sb06.otboo.security.resolver.RoleAuthorizationInterceptor;
import codeit.sb06.otboo.security.jwt.JwtRegistry;
import codeit.sb06.otboo.security.jwt.JwtTokenProvider;
import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.service.AuthServiceImpl;
import codeit.sb06.otboo.user.service.UserServiceImpl;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
    WebMvcConfig.class,
    RoleAuthorizationInterceptor.class,
    CurrentUserIdArgumentResolver.class
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthServiceImpl authServiceImpl;

    @MockitoBean
    private UserServiceImpl userServiceImpl;

    @MockitoBean
    private JwtRegistry jwtRegistry;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @TestConfiguration
    static class RoleHierarchyTestConfig {
        @Bean
        RoleHierarchy roleHierarchy() {
            return RoleHierarchyImpl.withDefaultRolePrefix()
                .role(Role.ADMIN.name())
                .implies(Role.USER.name())
                .build();
        }
    }

    @Test
    void refreshSetsNewRefreshCookieAndReturnsAccessToken() throws Exception {
        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "user@example.com",
            LocalDateTime.of(2026, 1, 1, 0, 0),
            Role.USER.name(),
            false
        );
        JwtInformation info = new JwtInformation(userDto, "access-new", "refresh-new");

        when(authServiceImpl.refreshToken(eq("refresh-old"))).thenReturn(info);
        when(jwtTokenProvider.generateRefreshTokenCookie(eq("refresh-new")))
            .thenReturn(new Cookie("REFRESH_TOKEN", "refresh-new"));

        mockMvc.perform(
                post("/api/auth/refresh")
                    .cookie(new Cookie("REFRESH_TOKEN", "refresh-old"))
            )
            .andExpect(status().isOk())
            .andExpect(cookie().value("REFRESH_TOKEN", "refresh-new"))
            .andExpect(jsonPath("$.accessToken").value("access-new"))
            .andExpect(jsonPath("$.userDto.email").value("user@example.com"));
    }

    @Test
    void resetPasswordReturnsNoContent() throws Exception {
        mockMvc.perform(
                post("/api/auth/reset-password")
                    .contentType("application/json")
                    .content("{\"email\":\"user@example.com\"}")
            )
            .andExpect(status().isNoContent());

        verify(userServiceImpl).send(eq("user@example.com"));
    }
}
