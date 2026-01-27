package codeit.sb06.otboo.comments.dto;

import java.util.UUID;

public record CommentCreateRequest(
    UUID feedId,
    UUID authorId,
    String content
) {

}
