package codeit.sb06.otboo.feed.dto;

import java.util.Locale;

public enum FeedSortBy {
  LIKECOUNT("likeCount"),
  CREATEDAT("createdAt");

  private final String apiValue;

  FeedSortBy(String apiValue) {
    this.apiValue = apiValue;
  }

  public String apiValue() {
    return apiValue;
  }

  public static FeedSortBy from(String rawValue) {
    if (rawValue == null || rawValue.isBlank()) {
      return CREATEDAT;
    }

    String normalized = rawValue
        .trim()
        .replace("_", "")
        .toUpperCase(Locale.ROOT);

    return switch (normalized) {
      case "CREATEDAT" -> CREATEDAT;
      case "LIKECOUNT" -> LIKECOUNT;
      default -> CREATEDAT;
    };
  }
}
