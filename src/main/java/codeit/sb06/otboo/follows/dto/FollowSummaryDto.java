package codeit.sb06.otboo.follows.dto;

public record FollowSummaryDto(
    String followeeId,
    Long followerCount,
    Long followingCount,
    boolean followedByMe,
    String followedByMeId,
    boolean followingMe
) {

}
