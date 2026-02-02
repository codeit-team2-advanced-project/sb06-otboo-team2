package codeit.sb06.otboo.comment.dto;

import codeit.sb06.otboo.comment.entity.Comment;
import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDto(
    UUID id,
    LocalDateTime createdAt,
    UUID feedId,
    AuthorDto author,
    String content
) {
  public static CommentDto of(
      Comment comment,
      AuthorDto author
  ){
    return new CommentDto(
        comment.getId(),
        comment.getCreatedAt(),
        comment.getFeed().getId(),
        author,
        comment.getContent()
        );
  }
}
