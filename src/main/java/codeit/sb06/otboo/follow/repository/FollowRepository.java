package codeit.sb06.otboo.follow.repository;

import codeit.sb06.otboo.follow.entity.Follow;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, UUID>, FollowQueryRepository {

  // 팔로워 수 -> 나를 팔로우하는사람 아이디 즉 나를 팔로우 하는 사람의 아이디 수
  Long countByFollowerId(UUID followerId);

  //팔로잉 수 -> 내가 팔로우하는 사람 즉 내가 팔로우하는 사람의 아이디 개수
  Long countByFolloweeId(UUID followeeId);

  Optional<Follow> findByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);

}
