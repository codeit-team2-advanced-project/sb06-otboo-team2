package codeit.sb06.otboo.comments.dto;

public record CommentCreateRequest(
    String feedId,
    String authorId,
    String content
) {

}
