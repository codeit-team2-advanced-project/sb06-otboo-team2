package codeit.sb06.otboo.follow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.follow.dto.FollowCreateRequest;
import codeit.sb06.otboo.follow.dto.FollowDto;
import codeit.sb06.otboo.follow.entity.Follow;
import codeit.sb06.otboo.follow.repository.FollowRepository;
import codeit.sb06.otboo.follow.service.BasicFollowService;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FollowCreateServiceTest {

  @InjectMocks
  private BasicFollowService basicFollowService;

  @Mock
  private FollowRepository followRepository;

  @Mock
  private UserRepository userRepository;

  UUID followerId, followeeId;
  User followee, follower;

  @BeforeEach
  void setUp() {
    followerId = UUID.randomUUID();
    followeeId = UUID.randomUUID();

    follower = mock(User.class);
    when(follower.getId()).thenReturn(followerId);
    when(follower.getName()).thenReturn("테스트 팔로워");
    when(follower.getProfileImageUrl()).thenReturn("테스트 팔로워 사진");

    followee = mock(User.class);
    when(followee.getId()).thenReturn(followeeId);
    when(followee.getName()).thenReturn("테스트 팔로위");
    when(followee.getProfileImageUrl()).thenReturn("테스트 팔로위 사진");
  }

  @Test
  void createFollow_success() {

    //given
    FollowCreateRequest followCreateRequest = new FollowCreateRequest(followeeId,followerId);
    when(userRepository.findById(followeeId)).thenReturn(Optional.of(followee));
    when(userRepository.findById(followerId)).thenReturn(Optional.of(follower));
    when(followRepository.save(any(Follow.class))).thenAnswer(invocation -> invocation.getArgument(0));

    //when
    FollowDto result = basicFollowService.createFollow(followCreateRequest);

    //then
    // 결과가 널이 아닌지
    assertNotNull(result);
    // 저장된 값이 같은지
    assertEquals(followeeId, result.followee().userId());
    assertEquals(followerId, result.follower().userId());
    // 딱 한번 호출됬는지
    verify(followRepository,times(1)).save(any(Follow.class));
  }
}
