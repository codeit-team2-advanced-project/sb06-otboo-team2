package codeit.sb06.otboo.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import codeit.sb06.otboo.comment.entity.Comment;
import codeit.sb06.otboo.config.JpaAuditingConfig;
import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.feed.repository.FeedRepository;
import codeit.sb06.otboo.user.dto.request.UserCreateRequest;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
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

    for (int i = 24; i >=0; i--) {
      commentRepository.save(
          Comment.builder()
              .feed(feed)
              .user(user)
              .content("테스트 댓글 " + (i + 1))
              .build()
      );
    }
  }

  // 첫 페이지 조회
  @Test
  void findFirstPage() {

      //when
     List<Comment> result =
         commentRepository.findCommentListByCursor(feed.getId(), null,null, 20);

     // then
      assertThat(result).hasSize(20);
      assertThat(result.get(0).getContent()).isEqualTo("테스트 댓글 1");
      assertThat(result.get(19).getContent()).isEqualTo("테스트 댓글 20");

    }

  // 다음 페이지 조회
  @Test
  void findNextPage() {

    //given
    List<Comment> firstPage =
        commentRepository.findCommentListByCursor(feed.getId(), null, null, 20);

    Comment lastComment = firstPage.get(firstPage.size()-1);
    UUID lastCommentId = firstPage.get(firstPage.size()-1).getId();
    LocalDateTime lastCreatedAt = lastComment.getCreatedAt();

    // when
    List<Comment> nextPage =
        commentRepository.findCommentListByCursor(feed.getId(), lastCreatedAt, lastCommentId, 20);

    //then
    assertThat(nextPage).hasSize(5);
    assertThat(nextPage.get(0).getContent()).isEqualTo("테스트 댓글 21");
    assertThat(nextPage.get(3).getContent()).isEqualTo("테스트 댓글 24");

    }
}