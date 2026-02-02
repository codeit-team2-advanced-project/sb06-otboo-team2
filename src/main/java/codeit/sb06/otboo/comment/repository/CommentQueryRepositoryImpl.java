package codeit.sb06.otboo.comment.repository;

import static codeit.sb06.otboo.comment.entity.QComment.comment;

import codeit.sb06.otboo.comment.entity.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Comment> findCommentListByCursor(UUID feedId, UUID idAfter, int limit) {
    var query = jpaQueryFactory.selectFrom(comment)
        .where(comment.feed.id.eq(feedId))
        .orderBy(comment.createdAt.desc())
        .limit(limit);

    if(idAfter != null){
      query = query.where(comment.id.gt(idAfter));
    }
    return query.fetch();
  }
}
