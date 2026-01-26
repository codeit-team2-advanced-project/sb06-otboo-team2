package codeit.sb06.otboo.follows.dto;

public record FollowCreateRequest(
    String followeeId,
    String followerId
) {

}
