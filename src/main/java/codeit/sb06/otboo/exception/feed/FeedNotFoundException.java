package codeit.sb06.otboo.exception.feed;

import java.util.UUID;

public class FeedNotFoundException extends FeedException {

  private static final String DEFAULT_MESSAGE = "Feed not found";

  public FeedNotFoundException(UUID feedId) {
    super(DEFAULT_MESSAGE, 404);
    addDetail("feedId", feedId);
  }

  public FeedNotFoundException(UUID feedId, Throwable cause) {
    super(DEFAULT_MESSAGE, cause, 404);
    addDetail("feedId", feedId);
  }
}
