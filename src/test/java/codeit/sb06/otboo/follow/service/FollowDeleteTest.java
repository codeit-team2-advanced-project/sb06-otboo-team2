package codeit.sb06.otboo.follow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.exception.follow.FollowCancelFailException;
import codeit.sb06.otboo.follow.entity.Follow;
import codeit.sb06.otboo.follow.repository.FollowRepository;
import codeit.sb06.otboo.profile.entity.Profile;
import codeit.sb06.otboo.profile.repository.ProfileRepository;
import codeit.sb06.otboo.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FollowDeleteTest {

  @InjectMocks
  private BasicFollowService basicFollowService;

  @Mock
  private FollowRepository followRepository;

  @Mock
  private ProfileRepository profileRepository;

  @Test
  void deleteFollow_success() {

    //given
    UUID followId = UUID.randomUUID();

    Follow follow = mock(Follow.class);
    User follower = mock(User.class);
    User followee = mock(User.class);
    Profile followerProfile = mock(Profile.class);
    Profile followeeProfile = mock(Profile.class);

    when(followRepository.findById(followId)).thenReturn(Optional.of(follow));
    when(follow.getFollower()).thenReturn(follower);
    when(follow.getFollowee()).thenReturn(followee);

    when(profileRepository.findByUserId(follower))
        .thenReturn(Optional.of(followerProfile));

    when(profileRepository.findByUserId(followee))
        .thenReturn(Optional.of(followeeProfile));

    doNothing().when(followRepository).deleteById(followId);

    //when
    basicFollowService.deleteFollow(followId);

    //then
    verify(followRepository,times(1)).deleteById(followId);
    verify(followerProfile,times(1)).decreaseFollowingCount();
    verify(followeeProfile,times(1)).decreaseFollowerCount();
  }

  @Test
  void deleteFollow_repositoryThrows_fail() {

    // given
    UUID followId = UUID.randomUUID();

    when(followRepository.findById(followId)).thenReturn(Optional.empty());

    // when
    FollowCancelFailException exception  = assertThrows(
        FollowCancelFailException.class,
        () -> basicFollowService.deleteFollow(followId)
    );

    //then
    assertEquals("팔로우 취소 실패", exception.getMessage());

    verify(followRepository,never()).deleteById(any());

  }
}
