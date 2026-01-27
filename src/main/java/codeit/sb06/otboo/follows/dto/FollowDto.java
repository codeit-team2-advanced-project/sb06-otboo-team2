package codeit.sb06.otboo.follows.dto;

import codeit.sb06.otboo.follows.domain.Follow;
import java.util.UUID;

public record FollowDto(
    UUID id,
    FolloweeDto followee,
    FollowerDto follower
) {
  public static FollowDto of(
      Follow follow,
      FolloweeDto followee,
      FollowerDto follower
  )
  {
    return new FollowDto(

        follow.getId(),
        followee,
        follower
  );
  }
}
