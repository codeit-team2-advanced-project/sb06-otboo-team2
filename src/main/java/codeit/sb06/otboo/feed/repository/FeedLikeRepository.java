package codeit.sb06.otboo.feed.repository;

import codeit.sb06.otboo.feed.entity.FeedLike;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedLikeRepository extends JpaRepository<FeedLike, UUID> {

  Optional<FeedLike> findByFeedIdAndUserId(UUID feedId, UUID userId);

  boolean existsByFeedIdAndUserId(UUID feedId, UUID userId);
}
