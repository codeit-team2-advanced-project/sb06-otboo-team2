package codeit.sb06.otboo.feed.entity;

import codeit.sb06.otboo.clothes.entity.Clothes;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.weather.entity.Weather;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feeds")
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Feed {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "content", nullable = true)
  private String content;

  @Column(name = "like_count", nullable = false)
  private Long likeCount = 0L;

  @Column(name = "comment_count", nullable = false)
  private int commentCount = 0;

  // MARK: - 유저 삭제 시 게시물 삭제 여부 (cascade)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "weather_id", nullable = false)
  private Weather weather;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "feed_id", nullable = false)
  private List<FeedClothes> feedClothes = new ArrayList<>();

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  private Feed(User user, Weather weather, String content, List<FeedClothes> feedClothes) {
    this.user = user;
    this.weather = weather;
    this.content = content;
    this.feedClothes = feedClothes;
  }

  public static Feed create(User user, Weather weather, List<Clothes> clothes, String content) {
    List<FeedClothes> mapped = clothes.stream()
        .map(FeedClothes::of)
        .toList();
    return new Feed(user, weather, content, new ArrayList<>(mapped));
  }

  public void updateContent(String content) {
    this.content = content;
  }

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }
}
