package codeit.sb06.otboo.comment.repository;

import codeit.sb06.otboo.comment.entity.Comment;
import java.util.List;
import java.util.UUID;

public interface CommentQueryRepository {
  List<Comment> findCommentListByCursor(UUID feedId,UUID idAfter, int limit);
}
