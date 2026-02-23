package codeit.sb06.otboo.follow.repository;

import static codeit.sb06.otboo.follow.entity.QFollow.follow;

import codeit.sb06.otboo.follow.entity.Follow;
import codeit.sb06.otboo.follow.entity.FollowDirection;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FollowQueryRepositoryImpl implements FollowQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Follow> findByCursor(FollowDirection followDirection, UUID userId,
      LocalDateTime lastCreatedAt, UUID idAfter, int limit, String nameLike) {

    JPAQuery<Follow> query = queryFactory.selectFrom(follow)
        .join(follow.followee).fetchJoin()
        .join(follow.follower).fetchJoin()
        .orderBy(
            follow.createdAt.desc(),
            follow.id.desc()
        )
        .limit(limit);

    if (followDirection == FollowDirection.FOLLOWER) {
      query.where(follow.followee.id.eq(userId));
    }
    else {
      query.where(follow.follower.id.eq(userId));
    }

    if (lastCreatedAt != null && idAfter != null) {
      query.where(
          follow.createdAt.lt(lastCreatedAt)
              .or(
                  follow.createdAt.eq(lastCreatedAt)
                      .and(follow.id.lt(idAfter))
              )
      );
    }
    if (nameLike != null && !nameLike.isBlank()) {
      if (followDirection == FollowDirection.FOLLOWER) {
        query.where(follow.follower.name.containsIgnoreCase(nameLike));
      } else {
        query.where(follow.followee.name.containsIgnoreCase(nameLike));
      }
    }

    return query.fetch();
  }

  @Override
  public Long countByCondition(FollowDirection followDirection, UUID userId, String nameLike) {
    var query = queryFactory
        .select(follow.count())
        .from(follow);

    if (followDirection == FollowDirection.FOLLOWER) {
      query.where(follow.followee.id.eq(userId));
    } else {
      query.where(follow.follower.id.eq(userId));
    }

    if (nameLike != null && !nameLike.isBlank()) {
      query.where(
          follow.followee.name.containsIgnoreCase(nameLike)
              .or(follow.follower.name.containsIgnoreCase(nameLike))
      );
    }

    return query.fetchOne();
  }
}
