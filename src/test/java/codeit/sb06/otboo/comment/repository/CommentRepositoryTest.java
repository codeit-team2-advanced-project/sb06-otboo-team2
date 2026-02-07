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
import codeit.sb06.otboo.weather.dto.weather.PrecipitationType;
import codeit.sb06.otboo.weather.dto.weather.SkyStatus;
import codeit.sb06.otboo.weather.dto.weather.WindStrength;
import codeit.sb06.otboo.weather.entity.Weather;
import codeit.sb06.otboo.weather.repository.WeatherRepository;
import java.lang.reflect.Field;
import java.time.LocalDate;
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

  @Autowired
  private WeatherRepository weatherRepository;

  private Feed feed;

  private User user;

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    user = userRepository.save(
        User.from(new UserCreateRequest("테스트 유저", "codeit1234@otboo.com", "pwd123"))
    );

    Weather weather = weatherRepository.save(Weather.builder()
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
        .build());

    feed = feedRepository.save(
        Feed.create(user, weather, List.of(), "테스트 피드")
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
      createdAtField.set(comment, LocalDateTime.now().minusMinutes(i));

      commentRepository.save(comment);
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