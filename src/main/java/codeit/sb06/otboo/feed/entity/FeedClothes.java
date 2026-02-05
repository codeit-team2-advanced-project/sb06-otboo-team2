package codeit.sb06.otboo.feed.entity;

import codeit.sb06.otboo.clothes.entity.Clothes;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feeds_clothes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedClothes {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "clothes_id", nullable = false)
  private Clothes clothes;

  private FeedClothes(Clothes clothes) {
    this.clothes = clothes;
  }

  public static FeedClothes of(Clothes clothes) {
    return new FeedClothes(clothes);
  }
}