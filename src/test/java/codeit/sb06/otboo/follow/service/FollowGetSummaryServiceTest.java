package codeit.sb06.otboo.follow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.exception.user.UserNotFoundException;
import codeit.sb06.otboo.follow.dto.FollowSummaryDto;
import codeit.sb06.otboo.follow.entity.Follow;
import codeit.sb06.otboo.follow.repository.FollowRepository;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FollowGetSummaryServiceTest {

  @InjectMocks
  private BasicFollowService basicFollowService;

  @Mock
  private FollowRepository followRepository;

  @Mock
  private UserRepository userRepository;

  // 스웨거와 프로토타입의 차이느 나를 힘들게한다...

  /***
   *  followeeId와 follwedByMeId랑 스웨거상 같길래 같나 했지만
   *  왜 또 프로토타입 상 에서는 다르냐
   * ***/
  //나는 팔로우 하는데 상대는 나를 팔로우 하지 않는 경우
  @Test
  void getFollowSummary_onlyFollowee() {
    // given
    UUID targetId = UUID.randomUUID();
    UUID myId = UUID.randomUUID();

    User targetUser = mock(User.class);

    when(userRepository.findById(targetId))
        .thenReturn(Optional.of(targetUser));

    Follow followedByMe = mock(Follow.class);

    UUID followId = UUID.randomUUID();

    when(followedByMe.getId()).thenReturn(followId);

    when(followRepository.findByFollowerIdAndFolloweeId(myId, targetId))
        .thenReturn(Optional.of(followedByMe));

    when(followRepository.findByFollowerIdAndFolloweeId(targetId, myId))
        .thenReturn(Optional.empty());

    //when
    FollowSummaryDto result = basicFollowService.getFollowSummary(targetId, myId);

    //then
    // 내가 팔로우하는 상대가 목표로 하는 아이디가 맞는지
    assertThat(result.followeeId()).isEqualTo(targetId);

    // 나에의해 팔로우 되는지
    assertThat(result.followedByMe()).isTrue();

    assertThat(result.followedByMeId()).isEqualTo(followId);

    // 나를 팔로우 하는지
    assertThat(result.followingMe()).isFalse();
  }

  // 상대만 나를 팔로우 하는경우
  @Test
  void getFollowSummary_onlyFollower() {

    //given
    UUID targetId = UUID.randomUUID();
    UUID myId = UUID.randomUUID();

    User targetUser = mock(User.class);

    Follow followedByTarget = mock(Follow.class);

    when(userRepository.findById(targetId))
        .thenReturn(Optional.of(targetUser));

    when(followRepository.findByFollowerIdAndFolloweeId(myId, targetId))
        .thenReturn(Optional.empty());

    when(followRepository.findByFollowerIdAndFolloweeId(targetId, myId))
        .thenReturn(Optional.of(followedByTarget));

    //when
    FollowSummaryDto result = basicFollowService.getFollowSummary(targetId, myId);

    //then
    // followedByMeId는 내가 팔로우할때 생김
    assertThat(result.followedByMeId()).isNull();

    //나를 팔로윙 하는지
    assertThat(result.followingMe()).isTrue();

    //내가 팔로우 하는지
    assertThat(result.followedByMe()).isFalse();

  }

  // 서로 팔로우 하는경우
  @Test
  void getFollowSummary_mutualFollower() {

    //given
    UUID targetId = UUID.randomUUID();
    UUID myId = UUID.randomUUID();

    User targetUser = mock(User.class);

    Follow followedByMe = mock(Follow.class);
    Follow followedByTarget = mock(Follow.class);

    UUID followId = UUID.randomUUID();
    when(followedByMe.getId()).thenReturn(followId);

    when(userRepository.findById(targetId))
        .thenReturn(Optional.of(targetUser));

    when(followRepository.findByFollowerIdAndFolloweeId(myId, targetId))
        .thenReturn(Optional.of(followedByMe));

    when(followRepository.findByFollowerIdAndFolloweeId(targetId, myId))
        .thenReturn(Optional.of(followedByTarget));

    //when
    FollowSummaryDto result = basicFollowService.getFollowSummary(targetId, myId);

    //then
    //내가 팔로우하는 상대가 목표로 하는 상대가 맞는지
    assertThat(result.followeeId()).isEqualTo(targetId);

    //팔로우 여부
    assertThat(result.followedByMe()).isTrue();
    assertThat(result.followingMe()).isTrue();

    //내가 팔로우했으니 생기는 followId가 같은지
    assertThat(result.followedByMeId()).isEqualTo(followId);

  }

  // 팔로우하는지 관계없이 팔로워, 팔로우 수 알기
  @Test
  void getFollowSummary_count() {

    //given
    UUID targetId = UUID.randomUUID();
    UUID myId = UUID.randomUUID();

    User targetUser = mock(User.class);

    when(userRepository.findById(targetId))
        .thenReturn(Optional.of(targetUser));

    when(followRepository.findByFollowerIdAndFolloweeId(myId, targetId))
        .thenReturn(Optional.empty());
    when(followRepository.findByFollowerIdAndFolloweeId(targetId, myId))
        .thenReturn(Optional.empty());

    when(followRepository.countByFollowerId(targetId))
        .thenReturn(123L);
    when(followRepository.countByFolloweeId(targetId))
        .thenReturn(45L);

    //when
    FollowSummaryDto result = basicFollowService.getFollowSummary(targetId, myId);

    //then
    assertThat(result.followerCount()).isEqualTo(123L);
    assertThat(result.followingCount()).isEqualTo(45L);

    assertThat(result.followedByMe()).isFalse();
    assertThat(result.followingMe()).isFalse();
    assertThat(result.followedByMeId()).isNull();


  }

  // 사용자가 없는 경우
  @Test
  void getFollowSummary_userNotFound() {
    //given
    UUID targetId = UUID.randomUUID();
    UUID myId = UUID.randomUUID();

    when(userRepository.findById(targetId))
        .thenReturn(Optional.empty());

    //then
    assertThatThrownBy(() ->
        basicFollowService.getFollowSummary(targetId, myId)
    ).isInstanceOf(UserNotFoundException.class);
  }
}
