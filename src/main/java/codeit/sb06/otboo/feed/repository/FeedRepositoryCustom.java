package codeit.sb06.otboo.feed.repository;

import codeit.sb06.otboo.feed.dto.FeedDtoCursorRequest;
import codeit.sb06.otboo.feed.entity.Feed;
import java.util.List;

public interface FeedRepositoryCustom {
  List<Feed> findFeedListByCursor(FeedDtoCursorRequest request, int limit);

  long countFeedList(FeedDtoCursorRequest request);
}
