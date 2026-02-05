package codeit.sb06.otboo.follow.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import codeit.sb06.otboo.follow.dto.FollowCreateRequest;
import codeit.sb06.otboo.follow.dto.FollowDto;
import codeit.sb06.otboo.follow.dto.FolloweeDto;
import codeit.sb06.otboo.follow.dto.FollowerDto;
import codeit.sb06.otboo.follow.service.FollowService;
import codeit.sb06.otboo.security.jwt.JwtAuthenticationFilter;
import codeit.sb06.otboo.security.jwt.JwtTokenProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FollowController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FollowCreateResponseTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  RoleHierarchy roleHierarchy;

  @MockitoBean
  JwtTokenProvider jwtTokenProvider;

  @MockitoBean
  JwtAuthenticationFilter jwtAuthenticationFilter;

  @MockitoBean
  FollowService followService;

  @Autowired
  ObjectMapper objectMapper;

  @Test
  void follow_success_201response() throws Exception {

    // given
      UUID followerId = UUID.randomUUID();
      UUID followeeId = UUID.randomUUID();
      UUID followId = UUID.randomUUID();

      FollowCreateRequest request = new FollowCreateRequest(followeeId, followerId);

      FollowDto response = new FollowDto(
          followId,
          new FolloweeDto(followeeId, "테스트 팔로위", "test1"),
          new FollowerDto(followerId, "테스트 팔로워", "test2")
      );

      when(followService.createFollow(any(FollowCreateRequest.class)))
          .thenReturn(response);

      //then
      mockMvc.perform(post("/api/follows")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(followId.toString()))
          .andExpect(jsonPath("$.followee.userId").value(followeeId.toString()))
          .andExpect(jsonPath("$.follower.userId").value(followerId.toString()));
    }
}
