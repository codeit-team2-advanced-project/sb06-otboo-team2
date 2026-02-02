package codeit.sb06.otboo.feed.dto;

import codeit.sb06.otboo.feed.entity.Feed;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public record FeedDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    AuthorDto author,
//    WeatherSummaryDto weather,
//    List<OotdDto> ootds,
    String content,
    long likeCount,
    int commentCount,
    boolean likedByMe
) {
  public static FeedDto from(Feed feed) {
    Instant created = feed.getCreatedAt() == null
        ? null
        : feed.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant();
    Instant updated = feed.getUpdatedAt() == null
        ? null
        : feed.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant();
    AuthorDto author = new AuthorDto(
        feed.getUser().getId(),
        feed.getUser().getName(),
        feed.getUser().getProfileImageUrl()
    );
    return new FeedDto(
        feed.getId(),
        created,
        updated,
        author,
        feed.getContent(),
        feed.getLikeCount(),
        feed.getCommentCount(),
        false
    );
  }
}
