package codeit.sb06.otboo.comment.repository;

import codeit.sb06.otboo.comment.entity.Comment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CommentQueryRepository {
  List<Comment> findCommentListByCursor(UUID feedId, LocalDateTime lastCreatedAt,UUID idAfter, int limit);
}
