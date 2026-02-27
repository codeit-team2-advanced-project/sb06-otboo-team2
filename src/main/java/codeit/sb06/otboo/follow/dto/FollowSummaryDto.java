package codeit.sb06.otboo.follow.dto;

import codeit.sb06.otboo.follow.entity.Follow;
import java.util.Optional;
import java.util.UUID;

public record FollowSummaryDto(
    UUID followeeId,
    Long followerCount,
    Long followingCount,
    boolean followedByMe,
    UUID followedByMeId,
    boolean followingMe
) {

  public static FollowSummaryDto of(
      UUID followeeId,
      Long followerCount,
      Long followingCount,
      Optional<Follow> followedByMe,
      Optional<Follow> followingMe
  ) {
    return new FollowSummaryDto(
        followeeId,
        followerCount,
        followingCount,
        followedByMe.isPresent(),
        followedByMe.map(Follow::getId).orElse(null),
        followingMe.isPresent()
    );
  }
}
