package codeit.sb06.otboo.feed.entity;

import codeit.sb06.otboo.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author",nullable = false)
  private User user;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Builder
  public Feed(
      String content,
      User user
  ){
    this.content = content;
    this.user = user;
  }
}
