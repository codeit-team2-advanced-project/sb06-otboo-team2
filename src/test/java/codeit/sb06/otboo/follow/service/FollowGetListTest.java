package codeit.sb06.otboo.follow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.follow.dto.FollowListResponse;
import codeit.sb06.otboo.follow.entity.Follow;
import codeit.sb06.otboo.follow.entity.FollowDirection;
import codeit.sb06.otboo.follow.repository.FollowRepository;
import codeit.sb06.otboo.user.dto.request.UserCreateRequest;
import codeit.sb06.otboo.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class FollowGetListTest {

  @InjectMocks
  private BasicFollowService basicFollowService;

  @Mock
  private FollowRepository followRepository;

  private UUID userId;
  private int limit;
  private LocalDateTime now;
  private Follow f1,f2,f3,f4;

  private User follower;
  private User followee;

  @BeforeEach
  public void setup() {
    userId = UUID.randomUUID();
    limit = 2;
    now = LocalDateTime.now();

    //팔로워
    UserCreateRequest followerRequest =
        new UserCreateRequest("follower", "follower@test.com",null);

    follower = User.from(followerRequest);
    ReflectionTestUtils.setField(follower, "id", userId);

    // 팔로우 당하는 사람 팔로위
    UserCreateRequest followeeRequest =
        new UserCreateRequest("followee", "followee@test.com",null);

    followee = User.from(followeeRequest);
    ReflectionTestUtils.setField(followee, "id", UUID.randomUUID());


    f1 = Follow.builder().follower(follower).followee(followee).build();
    f2 = Follow.builder().follower(follower).followee(followee).build();
    f3 = Follow.builder().follower(follower).followee(followee).build();
    f4 = Follow.builder().follower(follower).followee(followee).build();

    ReflectionTestUtils.setField(f1, "createdAt", now.minusSeconds(1));
    ReflectionTestUtils.setField(f2, "createdAt", now.minusSeconds(2));
    ReflectionTestUtils.setField(f3, "createdAt", now.minusSeconds(3));
    ReflectionTestUtils.setField(f4, "createdAt", now.minusSeconds(4));

    ReflectionTestUtils.setField(f1, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(f2, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(f3, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(f4, "id", UUID.randomUUID());

  }

  //팔로잉 목록 조회
  @Test
  void getFollow_firstPage() {
    // given
    when(followRepository.findByCursor(
        eq(FollowDirection.FOLLOWING),
        eq(userId),
        isNull(),
        isNull(),
        eq(limit + 1),
        isNull()
    )).thenReturn(List.of(f1, f2, f3));

    when(followRepository.countByCondition(
        FollowDirection.FOLLOWING,
        userId,
        null
    )).thenReturn(4L);

    // when
    FollowListResponse response =
        basicFollowService.getFollowList(
            FollowDirection.FOLLOWING,
            userId,
            null,
            null,
            limit,
            null
        );

    // then

    System.out.println("=== getFollow_firstPage 결과 ===");
    System.out.println("totalCount = " + response.totalCount());
    System.out.println("hasNext = " + response.hasNext());
    System.out.println("nextCursor = " + response.nextCursor());
    System.out.println("nextIdAfter = " + response.nextIdAfter());
    System.out.println("data size = " + response.data().size());

    response.data().forEach(f ->
            System.out.println("follow id = " + f.id())
    );

    assertTrue(response.hasNext());

    assertEquals(4L, response.totalCount());


    assertEquals(f2.getCreatedAt().toString(),response.nextCursor());

    assertEquals(f2.getId(), response.nextIdAfter());
  }


  //팔로잉 다음 페이지 조회
  @Test
  void getFollow_nextPage() {
    // given
    String cursor = f2.getCreatedAt().toString();
    UUID idAfter = f2.getId();

    when(followRepository.findByCursor(
        eq(FollowDirection.FOLLOWING),
        eq(userId),
        eq(f2.getCreatedAt()),
        eq(idAfter),
        eq(limit + 1),
        isNull()
    )).thenReturn(List.of(f3, f4));

    when(followRepository.countByCondition(
        FollowDirection.FOLLOWING,
        userId,
        null
    )).thenReturn(4L);

    // when
    FollowListResponse response =
        basicFollowService.getFollowList(
            FollowDirection.FOLLOWING,
            userId,
            cursor,
            idAfter,
            limit,
            null
        );

    // then
    assertFalse(response.hasNext());
    assertEquals(4L, response.totalCount());
    assertEquals(2, response.data().size());
    assertEquals(f3.getId(), response.data().get(0).id());
    assertEquals(f4.getId(), response.data().get(1).id());

    assertNull(response.nextCursor());
    assertNull(response.nextIdAfter());
  }

  @Test
  void getFollow_NonePage() {

    String cursor = f1.getCreatedAt().toString();
    UUID idAfter = f1.getId();

    when(followRepository.findByCursor(
        eq(FollowDirection.FOLLOWING),
        eq(userId),
        eq(f1.getCreatedAt()),
        eq(idAfter),
        eq(limit + 1),
        isNull()
    )).thenReturn(List.of());

    when(followRepository.countByCondition(
        FollowDirection.FOLLOWING,
        userId,
        null
    )).thenReturn(4L);

    // when
    FollowListResponse response =
        basicFollowService.getFollowList(
            FollowDirection.FOLLOWING,
            userId,
            cursor,
            idAfter,
            limit,
            null
        );

    // then
    assertEquals(0, response.data().size());

    assertFalse(response.hasNext());
    assertNull(response.nextCursor());
    assertNull(response.nextIdAfter());
  }


  // 팔로워 목록 첫페이지 조회
  @Test
  void getFollower_firstPage() {

    // given
    when(followRepository.findByCursor(
        eq(FollowDirection.FOLLOWER),
        eq(userId),
        isNull(),
        isNull(),
        eq(limit + 1),
        isNull()
    )).thenReturn(List.of(f1, f2, f3));

    when(followRepository.countByCondition(
        FollowDirection.FOLLOWER,
        userId,
        null
    )).thenReturn(4L);

    // when
    FollowListResponse response =
        basicFollowService.getFollowList(
            FollowDirection.FOLLOWER,
            userId,
            null,
            null,
            limit,
            null
        );

    // then
    assertTrue(response.hasNext());
    assertEquals(2, response.data().size());
    assertEquals(4L, response.totalCount());

    assertEquals(f2.getCreatedAt().toString(), response.nextCursor());
    assertEquals(f2.getId(), response.nextIdAfter());
  }

}
