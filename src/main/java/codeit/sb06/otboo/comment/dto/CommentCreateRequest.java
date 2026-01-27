package codeit.sb06.otboo.comment.dto;

import java.util.UUID;

public record CommentCreateRequest(
    UUID feedId,
    UUID authorId,
    String content
) {

}
