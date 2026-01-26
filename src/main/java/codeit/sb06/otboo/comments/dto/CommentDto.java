package codeit.sb06.otboo.comments.dto;

public record CommentDto(
    String id,
    String createdAt,
    String feedId,
    AuthorDto author,
    String content
) {

}
