package codeit.sb06.otboo.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import codeit.sb06.otboo.comment.entity.Comment;
import codeit.sb06.otboo.config.JpaAuditingConfig;
import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.feed.repository.FeedRepository;
import codeit.sb06.otboo.user.dto.request.UserCreateRequest;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Import(JpaAuditingConfig.class)
public class CommentRepositoryTest {

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FeedRepository feedRepository;

  private Feed feed;

  private User user;

  @BeforeEach
  void setUp() {
    user = userRepository.save(
        User.from(new UserCreateRequest("테스트 유저", "codeit1234@otboo.com", "pwd123"))
    );

    feed = feedRepository.save(
        Feed.builder()
            .content("테스트 피드")
            .user(user)
            .build()
    );

    for (int i = 25; i >=0; i--) {
      commentRepository.save(
          Comment.builder()
              .feed(feed)
              .user(user)
              .content("테스트 댓글 " + (i + 1))
              .build()
      );
    }
  }

  @Test
  void findFirstPage() {

    // given
    // 프로토 타입 상 limit 은 20으로 설정되었기에
    int limit = 20;

      //when
     List<Comment> result =
         commentRepository.findCommentListByCursor(feed.getId(), null, limit);

     // then
      assertThat(result).hasSize(20);
      assertThat(result.get(0).getContent()).isEqualTo("테스트 댓글 1");
      assertThat(result.get(19).getContent()).isEqualTo("테스트 댓글 20");

    }
}