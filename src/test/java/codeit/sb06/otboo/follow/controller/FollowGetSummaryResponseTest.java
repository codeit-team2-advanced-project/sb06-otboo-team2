package codeit.sb06.otboo.follow.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import codeit.sb06.otboo.follow.dto.FollowSummaryDto;
import codeit.sb06.otboo.follow.service.FollowService;
import codeit.sb06.otboo.security.CurrentUserIdArgumentResolver;
import codeit.sb06.otboo.security.OtbooUserDetails;
import codeit.sb06.otboo.security.RoleAuthorizationInterceptor;
import codeit.sb06.otboo.security.jwt.JwtRegistry;
import codeit.sb06.otboo.security.jwt.JwtTokenProvider;
import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FollowController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CurrentUserIdArgumentResolver.class)
public class FollowGetSummaryResponseTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  RoleAuthorizationInterceptor roleAuthorizationInterceptor;

  @MockitoBean
  JwtRegistry jwtRegistry;

  @MockitoBean
  RoleHierarchy roleHierarchy;

  @MockitoBean
  JwtTokenProvider jwtTokenProvider;

  @MockitoBean
  FollowService followService;

  @MockitoBean
  UserRepository userRepository;

  @BeforeEach
  void setUp(){

    when(roleAuthorizationInterceptor.preHandle(any(), any(), any()))
        .thenReturn(true);
  }

  @Test
  void getFollowSummary_success_200response() throws Exception {

    //given
    UUID targetId = UUID.randomUUID();
    UUID followId = UUID.randomUUID();
    UUID myId =  UUID.randomUUID();

    UserDto userDto = new UserDto(
        myId,
        "test@test.com",
        LocalDateTime.now(),
        "ROLE_USER",
        false
    );

    OtbooUserDetails userDetails = mock(OtbooUserDetails.class);
    when(userDetails.getUserDto()).thenReturn(userDto);

    Authentication auth =
        new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            List.of(() -> "ROLE_USER")
        );

    SecurityContextHolder.getContext().setAuthentication(auth);

    FollowSummaryDto dto = new FollowSummaryDto(
        targetId,
        10L,
        5L,
        true,
        followId,
        false
    );

    when(followService.getFollowSummary(eq(targetId), eq(myId)))
        .thenReturn(dto);


    //then
    mockMvc.perform(get("/api/follows/summary")
            .param("targetId", String.valueOf(targetId))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.followeeId").value(targetId.toString()))
        .andExpect(jsonPath("$.followerCount").value(10))
        .andExpect(jsonPath("$.followingCount").value(5))
        .andExpect(jsonPath("$.followedByMe").value(true))
        .andExpect(jsonPath("$.followedByMeId").value(followId.toString()))
        .andExpect(jsonPath("$.followingMe").value(false));
  }
}
