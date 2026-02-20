package codeit.sb06.otboo.follow.service;

import codeit.sb06.otboo.exception.RootException;
import codeit.sb06.otboo.exception.user.UserException;
import codeit.sb06.otboo.exception.user.UserNotFoundException;
import codeit.sb06.otboo.follow.dto.FollowCreateRequest;
import codeit.sb06.otboo.follow.dto.FollowDto;
import codeit.sb06.otboo.follow.dto.FollowSummaryDto;
import codeit.sb06.otboo.follow.dto.FolloweeDto;
import codeit.sb06.otboo.follow.dto.FollowerDto;
import codeit.sb06.otboo.follow.entity.Follow;
import codeit.sb06.otboo.follow.repository.FollowRepository;
import codeit.sb06.otboo.notification.publisher.NotificationEventPublisher;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicFollowService implements FollowService {

  private final FollowRepository followRepository;
  private final UserRepository userRepository;
  private final NotificationEventPublisher notificationEventPublisher;

  @Transactional
  @Override
  public FollowDto createFollow(FollowCreateRequest followCreateRequest) {

    User follower = userRepository.findById(followCreateRequest.followerId())
        .orElseThrow(UserNotFoundException::new);
    User followee = userRepository.findById(followCreateRequest.followeeId())
        .orElseThrow(UserNotFoundException::new);

    Follow follow = Follow.of(follower,followee);

    followRepository.save(follow);

    FolloweeDto followeeDto = new FolloweeDto(followee.getId(),followee.getName(), followee.getProfileImageUrl());
    FollowerDto followerDto = new FollowerDto(follower.getId(), follower.getName(),follower.getProfileImageUrl());

    notificationEventPublisher.publishFollowedEvent(followee.getId(), follower.getName());

    return FollowDto.of(follow, followeeDto, followerDto);
  }

  @Override
  public FollowSummaryDto getFollowSummary(UUID targetId, UUID myId) {

    userRepository.findById(targetId).orElseThrow(UserNotFoundException::new);

    //팔로위 팔로우 당하는거, 팔로워 팔로우 거는거

    //팔로워 수. 팔로워? -> 내가 팔로우하는
    Long followerCount = followRepository.countByFollowerId(targetId);
    //팔로잉 수  팔로잉 -> 나를 팔로우하는
    Long followCount = followRepository.countByFolloweeId(targetId);

    // 나에의해 팔로우되었는지
    Optional<Follow>  followedByMe= followRepository.findByFollowerIdAndFolloweeId(myId, targetId);

    // 나를 팔로우하는지
    Optional<Follow> followingMe= followRepository.findByFollowerIdAndFolloweeId(targetId, myId);

    return FollowSummaryDto.of(
        targetId,
        followerCount,
        followCount,
        followedByMe,
        followingMe

    );
  }
}
