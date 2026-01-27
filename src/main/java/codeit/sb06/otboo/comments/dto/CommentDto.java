package codeit.sb06.otboo.comments.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDto(
    UUID id,
    LocalDateTime createdAt,
    UUID feedId,
    AuthorDto author,
    String content
) {

}
