package codeit.sb06.otboo.follow.repository;

import codeit.sb06.otboo.follow.entity.Follow;
import codeit.sb06.otboo.follow.entity.FollowDirection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface FollowQueryRepository {
  List<Follow> findByCursor(
      FollowDirection followDirection,
      UUID userId,
      LocalDateTime lastCreatedAt,
      UUID idAfter,
      int limit,
      String nameLike
  );

  Long countByCondition(
      FollowDirection followDirection,
      UUID userId,
      String nameLike
  );
}
