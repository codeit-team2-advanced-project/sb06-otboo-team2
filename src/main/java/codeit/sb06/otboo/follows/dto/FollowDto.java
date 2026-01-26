package codeit.sb06.otboo.follows.dto;

public record FollowDto(
    String id,
    FolloweeDto followee,
    FollowerDto follower
) {

}
