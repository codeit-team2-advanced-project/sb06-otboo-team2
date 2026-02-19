package codeit.sb06.otboo.feed.search.document;

import codeit.sb06.otboo.feed.entity.Feed;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Document(indexName = "feeds")
public class FeedDocument {

  @Id
  private String id;

  @Field(type = FieldType.Text)
  private String content;

  @Field(type = FieldType.Keyword)
  private String authorId;

  @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
  private LocalDateTime createdAt;

  @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
  private LocalDateTime updatedAt;

  @Field(type = FieldType.Long)
  private long likeCount;

  @Field(type = FieldType.Integer)
  private int commentCount;

  @Builder
  private FeedDocument(
      String id,
      String content,
      String authorId,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      long likeCount,
      int commentCount
  ) {
    this.id = id;
    this.content = content;
    this.authorId = authorId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.likeCount = likeCount;
    this.commentCount = commentCount;
  }

  public static FeedDocument from(Feed feed) {
    UUID feedId = feed.getId();
    UUID userId = feed.getUser().getId();
    return FeedDocument.builder()
        .id(feedId == null ? null : feedId.toString())
        .content(feed.getContent())
        .authorId(userId == null ? null : userId.toString())
        .createdAt(feed.getCreatedAt())
        .updatedAt(feed.getUpdatedAt())
        .likeCount(feed.getLikeCount())
        .commentCount(feed.getCommentCount())
        .build();
  }
}
