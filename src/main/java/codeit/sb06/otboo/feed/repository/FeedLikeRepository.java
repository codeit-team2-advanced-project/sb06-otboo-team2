package codeit.sb06.otboo.feed.repository;

import codeit.sb06.otboo.feed.entity.FeedLike;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedLikeRepository extends JpaRepository<FeedLike, UUID> {

  Optional<FeedLike> findByFeedIdAndUserId(UUID feedId, UUID userId);

  boolean existsByFeedIdAndUserId(UUID feedId, UUID userId);

  @Query("select fl.feed.id from FeedLike fl where fl.user.id = :userId and fl.feed.id in :feedIds")
  List<UUID> findFeedIdsByUserIdAndFeedIdIn(
      @Param("userId") UUID userId,
      @Param("feedIds") List<UUID> feedIds
  );
}
