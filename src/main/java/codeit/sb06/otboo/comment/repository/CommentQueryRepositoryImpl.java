package codeit.sb06.otboo.comment.repository;

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
    return List.of();
  }
}
