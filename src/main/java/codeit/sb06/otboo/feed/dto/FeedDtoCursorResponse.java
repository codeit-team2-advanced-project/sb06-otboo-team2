package codeit.sb06.otboo.feed.dto;

import codeit.sb06.otboo.feed.entity.Feed;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record FeedDtoCursorResponse(
    List<FeedDto> data,
    String nextCursor,
    UUID nextIdAfter,
    boolean hasNext,
    long totalCount,
    String sortBy,
    String sortDirection
) {
  public static FeedDtoCursorResponse of(
      List<Feed> feeds,
      Set<UUID> likedFeedIds,
      String nextCursor,
      UUID nextIdAfter,
      boolean hasNext,
      long totalCount,
      FeedSortBy sortBy,
      FeedSortDirection sortDirection
  ) {
    List<FeedDto> data = feeds.stream()
        .map(feed -> FeedDto.from(feed, likedFeedIds.contains(feed.getId())))
        .toList();
    return new FeedDtoCursorResponse(
        data,
        nextCursor,
        nextIdAfter,
        hasNext,
        totalCount,
        sortBy.name(),
        sortDirection.name()
    );
  }
}
