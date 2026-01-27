package codeit.sb06.otboo.follows.dto;

import java.util.UUID;

public record FollowCreateRequest(
    UUID followeeId,
    UUID followerId
) {

}
