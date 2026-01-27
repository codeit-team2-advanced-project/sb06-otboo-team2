package codeit.sb06.otboo.follow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "follows",
uniqueConstraints = {
  @UniqueConstraint(columnNames = {"follower_id", "followee_id"})
  }
)
public class Follow {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "follower_id",nullable = false)
  private UUID followerId;

  @Column(name = "followee_id",nullable = false)
  private UUID followeeId;


  @Builder
  public Follow(UUID followerId, UUID followeeId) {
    this.followerId = followerId;
    this.followeeId = followeeId;
  }

  public static Follow of(
      UUID followerId,
      UUID followeeId
  ){
    if(followerId.equals(followeeId)){
      throw new IllegalArgumentException("자기 자신을 팔로우 할 수 없음");
    }
    return new Follow(followerId, followeeId);
  }
}
