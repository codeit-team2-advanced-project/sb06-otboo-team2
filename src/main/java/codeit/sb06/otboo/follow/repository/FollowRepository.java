package codeit.sb06.otboo.follow.repository;

import codeit.sb06.otboo.follow.entity.Follow;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, UUID>, FollowQueryRepository {

  List<Follow> findByFolloweeId(UUID followeeId);

  Optional<Follow> findByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);
}
