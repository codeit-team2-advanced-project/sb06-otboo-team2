package codeit.sb06.otboo.follow.dto;

import java.util.UUID;

public record FollowCreateRequest(
    UUID followeeId,
    UUID followerId
) {

}
