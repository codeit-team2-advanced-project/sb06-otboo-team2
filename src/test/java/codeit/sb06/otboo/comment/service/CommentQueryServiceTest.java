package codeit.sb06.otboo.comment.service;


import codeit.sb06.otboo.comment.repository.CommentRepository;
import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.feed.repository.FeedRepository;
import codeit.sb06.otboo.user.entity.User;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
  }

  @Test
  void getComments_firstPage(){

  }

  @Test
  void getComments_nextPage(){
  }

  @Test
  void getComments_NonePage(){
  }
}
