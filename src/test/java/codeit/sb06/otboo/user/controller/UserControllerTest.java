package codeit.sb06.otboo.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.user.dto.response.UserDtoCursorResponse;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.config.WebMvcConfig;
import codeit.sb06.otboo.security.CurrentUserIdArgumentResolver;
import codeit.sb06.otboo.security.LoginFailureHandler;
import codeit.sb06.otboo.security.RoleAuthorizationInterceptor;
import codeit.sb06.otboo.security.jwt.JwtAuthenticationFilter;
import codeit.sb06.otboo.security.jwt.JwtLoginSuccessHandler;
import codeit.sb06.otboo.security.jwt.JwtLogoutHandler;
import codeit.sb06.otboo.security.jwt.JwtTokenProvider;
import codeit.sb06.otboo.user.service.UserServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.TestConfiguration;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
    WebMvcConfig.class,
    RoleAuthorizationInterceptor.class,
    CurrentUserIdArgumentResolver.class
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userServiceImpl;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtLoginSuccessHandler jwtLoginSuccessHandler;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtLogoutHandler jwtLogoutHandler;

    @MockitoBean
    private LoginFailureHandler loginFailureHandler;

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

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createReturnsUserDto() throws Exception {
        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "user@example.com",
            LocalDateTime.of(2026, 1, 1, 0, 0),
            Role.USER.name(),
            false
        );
        when(userServiceImpl.create(any())).thenReturn(userDto);

        mockMvc.perform(
                post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"name\",\"email\":\"user@example.com\",\"password\":\"pass\"}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("user@example.com"))
            .andExpect(jsonPath("$.role").value("USER"))
            .andExpect(jsonPath("$.isLocked").value(false));
    }

    @Test
    void getReturnsCursorResponse() throws Exception {
        setAuthentication(Role.ADMIN);
        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "user@example.com",
            LocalDateTime.of(2026, 1, 1, 0, 0),
            Role.USER.name(),
            false
        );
        UserDtoCursorResponse response = new UserDtoCursorResponse(
            List.of(userDto),
            "2026-01-01T00:00",
            userDto.id().toString(),
            true,
            1,
            "createdAt",
            "DESC"
        );
        when(userServiceImpl.getUsersCursor(any())).thenReturn(response);

        mockMvc.perform(
                get("/api/users")
                    .param("limit", "1")
                    .param("sortBy", "createdAt")
                    .param("sortDirection", "DESC")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].email").value("user@example.com"))
            .andExpect(jsonPath("$.nextCursor").value("2026-01-01T00:00"))
            .andExpect(jsonPath("$.hasNext").value(true))
            .andExpect(jsonPath("$.totalCount").value(1))
            .andExpect(jsonPath("$.sortBy").value("createdAt"))
            .andExpect(jsonPath("$.sortDirection").value("DESC"));
    }

    @Test
    void getReturnsForbiddenWhenUserRoleIsNotAdmin() throws Exception {
        setAuthentication(Role.USER);
        mockMvc.perform(
                get("/api/users")
                    .param("limit", "1")
                    .param("sortBy", "createdAt")
                    .param("sortDirection", "DESC")
            )
            .andExpect(status().isForbidden());
    }

    @Test
    void updateRoleReturnsUserDto() throws Exception {
        setAuthentication(Role.ADMIN);
        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "admin@example.com",
            LocalDateTime.of(2026, 1, 1, 0, 0),
            Role.ADMIN.name(),
            false
        );
        when(userServiceImpl.changeUserRole(any(), any())).thenReturn(userDto);

        mockMvc.perform(
                patch("/api/users/{userId}/role", UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"role\":\"ADMIN\"}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void updateLockReturnsUserDto() throws Exception {
        setAuthentication(Role.ADMIN);
        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "user@example.com",
            LocalDateTime.of(2026, 1, 1, 0, 0),
            Role.USER.name(),
            true
        );
        when(userServiceImpl.changeLockStatus(any(), org.mockito.ArgumentMatchers.anyBoolean()))
            .thenReturn(userDto);

        mockMvc.perform(
                patch("/api/users/{userId}/lock", UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"locked\":true}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isLocked").value(true));
    }

    private void setAuthentication(Role role) {
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                "user",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
            );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
