package codeit.sb06.otboo.follow.dto;

import java.util.UUID;

public record FollowSummaryDto(
    UUID followeeId,
    Long followerCount,
    Long followingCount,
    boolean followedByMe,
    UUID followedByMeId,
    boolean followingMe
) {

}
