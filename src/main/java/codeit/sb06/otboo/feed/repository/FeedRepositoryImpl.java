package codeit.sb06.otboo.feed.repository;

import static codeit.sb06.otboo.feed.entity.QFeed.feed;

import codeit.sb06.otboo.feed.dto.FeedDtoCursorRequest;
import codeit.sb06.otboo.feed.dto.FeedSortBy;
import codeit.sb06.otboo.feed.dto.FeedSortDirection;
import codeit.sb06.otboo.feed.entity.Feed;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FeedRepositoryImpl implements FeedRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Feed> findFeedListByCursor(FeedDtoCursorRequest request, int limit) {
    BooleanBuilder where = buildWhere(request, true);
    OrderSpecifier<?> primaryOrder = orderSpecifier(request);
    OrderSpecifier<?> secondaryOrder = new OrderSpecifier<>(
        isDesc(request) ? Order.DESC : Order.ASC,
        feed.id
    );

    return jpaQueryFactory.selectFrom(feed)
        .where(where)
        .orderBy(primaryOrder, secondaryOrder)
        .limit(limit)
        .fetch();
  }

  @Override
  public long countFeedList(FeedDtoCursorRequest request) {
    BooleanBuilder where = buildWhere(request, false);
    Long count = jpaQueryFactory.select(feed.count())
        .from(feed)
        .where(where)
        .fetchOne();
    return count == null ? 0L : count;
  }

  private BooleanBuilder buildWhere(FeedDtoCursorRequest request, boolean includeCursor) {
    BooleanBuilder where = new BooleanBuilder();

    if (request.keywordLike() != null && !request.keywordLike().isBlank()) {
      where.and(feed.content.containsIgnoreCase(request.keywordLike()));
    }
    if (request.skyStatusEqual() != null) {
      where.and(feed.weather.skyStatus.eq(request.skyStatusEqual()));
    }
    if (request.precipitationTypeEqual() != null) {
      where.and(feed.weather.precipitationType.eq(request.precipitationTypeEqual()));
    }
    if (request.authorIdEqual() != null) {
      where.and(feed.user.id.eq(request.authorIdEqual()));
    }

    if (includeCursor && request.cursor() != null && request.idAfter() != null) {
      addCursorPredicate(where, request);
    }
    return where;
  }

  private OrderSpecifier<?> orderSpecifier(FeedDtoCursorRequest request) {
    Order order = isDesc(request) ? Order.DESC : Order.ASC;
    FeedSortBy sortBy = request.sortBy();
    if (sortBy == FeedSortBy.likeCount) {
      return new OrderSpecifier<>(order, feed.likeCount);
    }
    return new OrderSpecifier<>(order, feed.createdAt);
  }

  private void addCursorPredicate(BooleanBuilder where, FeedDtoCursorRequest request) {
    FeedSortBy sortBy = request.sortBy();
    boolean desc = isDesc(request);
    UUID idAfter = request.idAfter();
    String cursor = request.cursor();

    if (sortBy == FeedSortBy.likeCount) {
      try {
        long likeCursor = Long.parseLong(cursor);
        where.and(
            desc
                ? feed.likeCount.lt(likeCursor)
                    .or(feed.likeCount.eq(likeCursor).and(feed.id.lt(idAfter)))
                : feed.likeCount.gt(likeCursor)
                    .or(feed.likeCount.eq(likeCursor).and(feed.id.gt(idAfter)))
        );
      } catch (NumberFormatException ignored) {
        // Ignore invalid cursor.
      }
      return;
    }

    try {
      LocalDateTime dateCursor = LocalDateTime.parse(cursor);
      where.and(
          desc
              ? feed.createdAt.lt(dateCursor)
                  .or(feed.createdAt.eq(dateCursor).and(feed.id.lt(idAfter)))
              : feed.createdAt.gt(dateCursor)
                  .or(feed.createdAt.eq(dateCursor).and(feed.id.gt(idAfter)))
      );
    } catch (DateTimeParseException ignored) {
      // Ignore invalid cursor.
    }
  }

  private boolean isDesc(FeedDtoCursorRequest request) {
    FeedSortDirection direction = request.sortDirection();
    return direction == null || direction == FeedSortDirection.DESCENDING;
  }
}
