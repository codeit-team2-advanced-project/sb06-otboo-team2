package codeit.sb06.otboo.comment.repository;

import codeit.sb06.otboo.comment.entity.Comment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public interface CommentRepository extends JpaRepository<Comment, UUID> {
}