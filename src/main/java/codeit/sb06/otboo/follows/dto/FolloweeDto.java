package codeit.sb06.otboo.follows.dto;

import java.util.UUID;

public record FolloweeDto(
    UUID userId,
    String name,
    String profileImageUrl
) {

}
