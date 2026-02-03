package codeit.sb06.otboo.comment.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.comment.entity.Comment;
import codeit.sb06.otboo.comment.repository.CommentRepository;
import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.feed.repository.FeedRepository;
import codeit.sb06.otboo.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class CommentQueryServiceTest {

  @InjectMocks
  private BasicCommentService basicCommentService;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private FeedRepository feedRepository;

  UUID feedId, authorId;
  User author;
  Feed feed;

  Comment c1,c2,c3;

  @BeforeEach
  void setUp() {
    feedId = UUID.randomUUID();
    authorId = UUID.randomUUID();

    author = new User(
        authorId,
        "user@example.com",
        "test",
        null,
        false,
        LocalDateTime.now(),
        LocalDateTime.now(),
        null,
        "password"
    );

    feed = Feed.builder()
        .content("테스트 피드 내용")
        .user(author)
        .build();

    LocalDateTime createdAt = LocalDateTime.now();

    c1 = Comment.builder().content("테스트 댓글 1").feed(feed).user(author).build(); // 오래된 댓글
    c2 = Comment.builder().content("테스트 댓글 2").feed(feed).user(author).build();
    c3 = Comment.builder().content("테스트 댓글 3").feed(feed).user(author).build(); // 최신

    ReflectionTestUtils.setField(c1, "createdAt", createdAt.minusMinutes(3));
    ReflectionTestUtils.setField(c2, "createdAt", createdAt.minusMinutes(2));
    ReflectionTestUtils.setField(c3, "createdAt", createdAt.minusMinutes(1));

    ReflectionTestUtils.setField(c1, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(c2, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(c3, "id", UUID.randomUUID());
  }

  // 첫 페이지 조회 테스트
  @Test
  void getComments_firstPage(){

    //given
    int limit = 2;

    when(feedRepository.findById(feedId))
        .thenReturn(Optional.of(feed));

    when(commentRepository.findCommentListByCursor(
        eq(feedId),
        isNull(),
        isNull(),
        eq(limit + 1)
    )).thenReturn(List.of(c3, c2, c1));

    //when
    var response = basicCommentService.getComments(feedId, null, null, limit);

    //then
    assertEquals(2, response.data().size());
    assertTrue(response.hasNext());
    assertEquals("테스트 댓글 3", response.data().get(0).content());
    assertEquals("테스트 댓글 2", response.data().get(1).content());
    assertEquals(c2.getCreatedAt().toString(), response.nextCursor());
    assertEquals(c2.getId(), response.nextIdAfter());

  }

  @Test
  void getComments_nextPage(){
  }

  @Test
  void getComments_NonePage(){
  }
}
