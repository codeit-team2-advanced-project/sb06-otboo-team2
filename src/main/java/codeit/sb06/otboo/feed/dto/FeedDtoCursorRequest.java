package codeit.sb06.otboo.feed.dto;

import codeit.sb06.otboo.weather.dto.weather.PrecipitationType;
import codeit.sb06.otboo.weather.dto.weather.SkyStatus;
import java.util.UUID;

public record FeedDtoCursorRequest(
    String cursor,
    UUID idAfter,
    int limit,
    String sortBy,
    FeedSortDirection sortDirection,
    String keywordLike,
    SkyStatus skyStatusEqual,
    PrecipitationType precipitationTypeEqual,
    UUID authorIdEqual
) {
  public FeedDtoCursorRequest {
    if (limit <= 0) {
      limit = 10;
    }
    if (sortDirection == null) {
      sortDirection = FeedSortDirection.DESCENDING;
    }
  }

  public FeedSortBy resolveSortBy() {
    return FeedSortBy.from(sortBy);
  }
}
