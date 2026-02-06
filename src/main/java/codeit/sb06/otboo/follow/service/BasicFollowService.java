package codeit.sb06.otboo.follow.service;

import codeit.sb06.otboo.exception.RootException;
import codeit.sb06.otboo.exception.user.UserException;
import codeit.sb06.otboo.exception.user.UserNotFoundException;
import codeit.sb06.otboo.follow.dto.FollowCreateRequest;
import codeit.sb06.otboo.follow.dto.FollowDto;
import codeit.sb06.otboo.follow.dto.FolloweeDto;
import codeit.sb06.otboo.follow.dto.FollowerDto;
import codeit.sb06.otboo.follow.entity.Follow;
import codeit.sb06.otboo.follow.repository.FollowRepository;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicFollowService implements FollowService {

  private final FollowRepository followRepository;
  private final UserRepository userRepository;

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

    return FollowDto.of(follow, followeeDto, followerDto);
  }
}
