package codeit.sb06.otboo.feed.search.dto;

import codeit.sb06.otboo.feed.search.document.FeedDocument;
import java.time.LocalDateTime;
import java.util.UUID;

public record FeedSearchHit(
    UUID id,
    String content,
    UUID authorId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    long likeCount,
    int commentCount
) {

  public static FeedSearchHit from(FeedDocument document) {
    return new FeedSearchHit(
        UUID.fromString(document.getId()),
        document.getContent(),
        document.getAuthorId() == null ? null : UUID.fromString(document.getAuthorId()),
        document.getCreatedAt(),
        document.getUpdatedAt(),
        document.getLikeCount(),
        document.getCommentCount()
    );
  }
}
