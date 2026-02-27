package codeit.sb06.otboo.feed.dto;

import codeit.sb06.otboo.comment.dto.AuthorDto;
import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.feed.entity.FeedClothes;
import codeit.sb06.otboo.weather.dto.weather.WeatherSummaryDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FeedDto(
    UUID id,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    AuthorDto author,
    WeatherSummaryDto weather,
    List<OotdDto> ootds,
    String content,
    long likeCount,
    Long commentCount,
    boolean likedByMe
) {
  public static FeedDto from(Feed feed) {
    return from(feed, false);
  }

  public static FeedDto from(Feed feed, boolean likedByMe) {
    LocalDateTime created = feed.getCreatedAt();
    LocalDateTime updated = feed.getUpdatedAt();
    AuthorDto author = AuthorDto.of(feed.getUser());
    return new FeedDto(
        feed.getId(),
        created,
        updated,
        author,
        WeatherSummaryDto.from(feed.getWeather()),
        feed.getFeedClothes().stream()
            .map(FeedClothes::getClothes)
            .map(OotdDto::from)
            .toList(),
        feed.getContent(),
        feed.getLikeCount(),
        feed.getCommentCount(),
        likedByMe
    );
  }
}
