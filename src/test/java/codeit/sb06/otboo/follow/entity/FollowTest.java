package codeit.sb06.otboo.follow.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.user.entity.User;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class FollowTest {

  //팔로우 발생 테스트
  @Test
  void generateFollow() {

    // given
    User follower = mock(User.class);
    User followee = mock(User.class);

    UUID followerId = UUID.randomUUID();
    UUID followeeId = UUID.randomUUID();

    when(follower.getId()).thenReturn(followerId);
    when(followee.getId()).thenReturn(followeeId);

    // when
    Follow follow = Follow.of(follower, followee);

    // then
    assertNotNull(follow);
    assertEquals(follower, follow.getFollower());
    assertEquals(followee, follow.getFollowee());
  }

  // 자기자신이 팔로우 시
  @Test
  void follow_sameUser() {

    // given
    User user = mock(User.class);
    UUID userId = UUID.randomUUID();
    when(user.getId()).thenReturn(userId);

    // then
    assertThrows(IllegalArgumentException.class,
        () -> Follow.of(user, user));
  }
}
