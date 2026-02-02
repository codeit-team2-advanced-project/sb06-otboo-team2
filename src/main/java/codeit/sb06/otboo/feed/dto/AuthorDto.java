package codeit.sb06.otboo.feed.dto;

import java.util.UUID;

public record AuthorDto(
    UUID userId,
    String name,
    String profileImageUrl
) {
}