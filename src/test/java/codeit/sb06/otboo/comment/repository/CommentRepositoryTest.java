package codeit.sb06.otboo.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import codeit.sb06.otboo.comment.entity.Comment;
import codeit.sb06.otboo.config.JpaAuditingConfig;
import codeit.sb06.otboo.config.QueryDslConfig;
import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.feed.repository.FeedRepository;
import codeit.sb06.otboo.user.dto.request.UserCreateRequest;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.lang.reflect.Field;
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
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
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
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    user = userRepository.save(
        User.from(new UserCreateRequest("테스트 유저", "codeit1234@otboo.com", "pwd123"))
    );

    feed = feedRepository.save(
        Feed.builder()
            .content("테스트 피드")
            .user(user)
            .build()
    );

    for (int i = 0; i < 25; i++) {
      Comment comment = Comment.builder()
          .feed(feed)
          .user(user)
          .content("테스트 댓글 " + (i + 1))
          .build();

      commentRepository.save(comment);

      Field createdAtField = Comment.class.getDeclaredField("createdAt");
      createdAtField.setAccessible(true);
      createdAtField.set(comment, LocalDateTime.now().withNano(0).minusMinutes(i));

      commentRepository.save(comment);
    }
    long count = commentRepository.count();
    System.out.println("Total comments: " + count);
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

  /***
   *
   * 현재 소나큐브에서 발생하는 문제 -> 갯수 5개 나와야하는데 6개 나온다고 함
   *
   * omment.createdAt.lt(lastCreatedAt) 조건에서, 본인(20번 댓글)이 본인보다 "작다"고 판정되어 결과에 포함되어 버리는 기현상이 발생할 수 있습니다.
   *
   * 이게 제일 유력해 보임
   *
   * 로컬 상에서 테스트 돌려보았을때는 정상적으로 이전페이지 20 다음 페이지 크기 5나오고 마지막 id  생성날짜 기준으로 다음 댓글 정상적으로 출력
   * 마지막 댓글이 20 이면 그다음 21부터 출력
   *
   * ***/

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