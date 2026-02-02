package codeit.sb06.otboo.follow.entity;

import codeit.sb06.otboo.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "follower_id", nullable = false)
  private User follower;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "followee_id", nullable = false)
  private User followee;


  @Builder
  public Follow(User follower, User followee) {
    this.follower = follower;
    this.followee = followee;
  }

  public static Follow of(
      User follower,
      User followee
  ){
    if(follower.getId().equals(followee.getId())){
      throw new IllegalArgumentException("자기 자신을 팔로우 할 수 없음");
    }
    return new Follow(follower, followee);
  }
}
