package codeit.sb06.otboo.comment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.comment.dto.CommentCreateRequest;
import codeit.sb06.otboo.comment.dto.CommentDto;
import codeit.sb06.otboo.comment.entity.Comment;
import codeit.sb06.otboo.comment.repository.CommentRepository;
import codeit.sb06.otboo.exception.feed.FeedNotFoundException;
import codeit.sb06.otboo.exception.user.UserNotFoundException;
import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.feed.repository.FeedRepository;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.entity.Provider;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.weather.dto.weather.PrecipitationType;
import codeit.sb06.otboo.weather.dto.weather.SkyStatus;
import codeit.sb06.otboo.weather.dto.weather.WindStrength;
import codeit.sb06.otboo.weather.entity.Weather;
import java.time.LocalDate;
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

@ExtendWith(MockitoExtension.class)
public class CommentCreateServiceTest {

  @InjectMocks
  private BasicCommentService basicCommentService;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private FeedRepository feedRepository;


  UUID feedId, authorId;
  User author;
  Feed feed;
  Weather weather;

  @BeforeEach
  void setUp() {
    feedId = UUID.randomUUID();
    authorId = UUID.randomUUID();

    author = new User(
        authorId,
        "user@example.com",
        "test",
        Provider.LOCAL,
        null,
        false,
        LocalDateTime.now(),
        LocalDateTime.now(),
        null,
        "password",
        null,
        null
    );

    weather = Weather.builder()
        .skyStatus(SkyStatus.CLEAR)
        .precipitationType(PrecipitationType.NONE)
        .precipitationAmount(0.0)
        .precipitationProbability(0.0)
        .tempCurrent(20.0)
        .tempMin(18.0)
        .tempMax(22.0)
        .humidity(50.0)
        .windSpeed(1.0)
        .windStrength(WindStrength.WEAK)
        .date(LocalDate.now())
        .latitude(37.0)
        .longitude(127.0)
        .forecastAt(LocalDateTime.now())
        .createdAt(LocalDateTime.now())
        .build();

    feed = Feed.create(author, weather, List.of(), "테스트 피드 내용");
  }

  // 댓글 성공 테스트
  @Test
  void createCommentTest(){

    //given

    CommentCreateRequest request =
        new CommentCreateRequest(feedId, authorId,"테스트 댓글 내용");

    Comment savedComment = Comment.builder()
        .content("테스트 댓글 내용")
        .feed(feed)
        .user(author)
        .build();


    when(feedRepository.findById(eq(feedId)))
        .thenReturn(Optional.of(feed));

    when(userRepository.findById(authorId))
        .thenReturn(Optional.of(author));

    when(commentRepository.save(any(Comment.class)))
        .thenReturn(savedComment);

    when(commentRepository.countByFeedId(feedId))
        .thenReturn(1L);

    //when

    CommentDto result = basicCommentService.createComment(feedId,request);


    //then
    verify(commentRepository).save(any(Comment.class));
    assertNotNull(result);
    assertEquals("테스트 댓글 내용",result.content());
    assertEquals(authorId, result.author().userId());
    assertEquals("test", result.author().name());
    assertEquals(1L, feed.getCommentCount());

  }

  // 피드 Id 없을때

  @Test
  void createThrowsFeedNotFound() {

    //given

    CommentCreateRequest request =
        new CommentCreateRequest(feedId, authorId, "테스트 댓글 생성");

    when(feedRepository.findById(eq(feedId)))
        .thenReturn(Optional.empty());

    //then
    assertThrows(
        FeedNotFoundException.class,
        () -> basicCommentService.createComment(feedId, request)
    );
  }

  // 사용자 없을때(사용자 id없을때)
  @Test
  void createThrowsUserNotFound() {

    // given

    CommentCreateRequest request =
        new CommentCreateRequest(feedId, authorId, "테스트 댓글 생성");

    when(feedRepository.findById(eq(feedId)))
        .thenReturn(Optional.of(feed));

    when(userRepository.findById(eq(authorId)))
        .thenReturn(Optional.empty());

    // then
    assertThrows(
        UserNotFoundException.class,
        () -> basicCommentService.createComment(feedId, request)
    );
  }
}
