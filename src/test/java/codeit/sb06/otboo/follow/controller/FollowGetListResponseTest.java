package codeit.sb06.otboo.follow.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import codeit.sb06.otboo.follow.dto.FollowDto;
import codeit.sb06.otboo.follow.dto.FollowListResponse;
import codeit.sb06.otboo.follow.dto.FolloweeDto;
import codeit.sb06.otboo.follow.dto.FollowerDto;
import codeit.sb06.otboo.follow.entity.FollowDirection;
import codeit.sb06.otboo.follow.service.FollowService;
import codeit.sb06.otboo.security.jwt.JwtAuthenticationFilter;
import codeit.sb06.otboo.security.jwt.JwtTokenProvider;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FollowController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class FollowGetListResponseTest {

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

  @Test
  void getFollowingList_success_200response() throws Exception {

    int limit = 2;

    UUID followeeId1 = UUID.randomUUID();
    UUID followeeId2 = UUID.randomUUID();
    UUID followerId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();


    FolloweeDto followee1 = new FolloweeDto(followeeId1, "test1", null);
    FolloweeDto followee2 = new FolloweeDto(followeeId2, "test2", null);
    FollowerDto follower = new FollowerDto(followerId, "follower", null);


    FollowDto f1 = new FollowDto(UUID.randomUUID(), followee1, follower);
    FollowDto f2 = new FollowDto(UUID.randomUUID(), followee2, follower);

    FollowListResponse followListResponse = new FollowListResponse(
        List.of(f1, f2),
        now.minusMinutes(2).toString(),
        f2.id(),
        true,
        5L,
        "createdAt",
        "DESCENDING"
    );

    // 첫페이지 가정
    when(followService.getFollowList(
        eq(FollowDirection.FOLLOWING),
        eq(followerId),
        isNull(),
        isNull(),
        eq(limit),
        isNull()
    )).thenReturn(followListResponse);

    mockMvc.perform(get("/api/follows/followings")
            .param("followerId", followerId.toString())
            .param("limit", String.valueOf(limit)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].followee.userId").value(followeeId1.toString()))
        .andExpect(jsonPath("$.data[0].followee.name").value("test1"))
        .andExpect(jsonPath("$.data[1].followee.userId").value(followeeId2.toString()))
        .andExpect(jsonPath("$.data[1].followee.name").value("test2"))
        .andExpect(jsonPath("$.hasNext").value(true))
        .andExpect(jsonPath("$.nextCursor").value(now.minusMinutes(2).toString()))
        .andExpect(jsonPath("$.nextIdAfter").value(f2.id().toString()))
        .andExpect(jsonPath("$.totalCount").value(5));
  }


  @Test
  void getFollowerList_success_200response() throws Exception {

    int limit = 2;

    UUID followerId1 = UUID.randomUUID();
    UUID followerId2 = UUID.randomUUID();
    UUID followeeId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();


    FollowerDto follower1 = new FollowerDto(followerId1, "test1", null);
    FollowerDto follower2 = new FollowerDto(followerId2, "test2", null);
    FolloweeDto followee = new FolloweeDto(followeeId, "followee", null);


    FollowDto f1 = new FollowDto(UUID.randomUUID(), followee, follower1);
    FollowDto f2 = new FollowDto(UUID.randomUUID(), followee, follower2);


    FollowListResponse followListResponse = new FollowListResponse(
        List.of(f1, f2),
        now.minusMinutes(2).toString(),
        f2.id(),
        true,
        5L,
        "createdAt",
        "DESCENDING"
    );

    when(followService.getFollowList(
        eq(FollowDirection.FOLLOWER),
        eq(followeeId),
        isNull(),
        isNull(),
        eq(limit),
        isNull()
    )).thenReturn(followListResponse);

    mockMvc.perform(get("/api/follows/followers")
            .param("followeeId", followeeId.toString())
            .param("limit", String.valueOf(limit)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].follower.userId").value(followerId1.toString()))
        .andExpect(jsonPath("$.data[0].follower.name").value("test1"))
        .andExpect(jsonPath("$.data[1].follower.userId").value(followerId2.toString()))
        .andExpect(jsonPath("$.data[1].follower.name").value("test2"))
        .andExpect(jsonPath("$.hasNext").value(true))
        .andExpect(jsonPath("$.nextCursor").value(now.minusMinutes(2).toString()))
        .andExpect(jsonPath("$.nextIdAfter").value(f2.id().toString()))
        .andExpect(jsonPath("$.totalCount").value(5));
  }
  @Test
  void getFollowingList_fail_400response_noId() throws Exception {

    mockMvc.perform(get("/api/follows/followings")
            .param("limit", "2"))
        .andExpect(status().isBadRequest());
  }

}
