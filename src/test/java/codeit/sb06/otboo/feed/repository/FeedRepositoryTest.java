package codeit.sb06.otboo.feed.repository;

import static org.assertj.core.api.Assertions.assertThat;

import codeit.sb06.otboo.config.JpaAuditingConfig;
import codeit.sb06.otboo.feed.dto.FeedDtoCursorRequest;
import codeit.sb06.otboo.feed.dto.FeedSortBy;
import codeit.sb06.otboo.feed.dto.FeedSortDirection;
import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.weather.dto.weather.PrecipitationType;
import codeit.sb06.otboo.weather.dto.weather.SkyStatus;
import codeit.sb06.otboo.weather.dto.weather.WindStrength;
import codeit.sb06.otboo.weather.entity.Weather;
import codeit.sb06.otboo.weather.repository.WeatherRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class FeedRepositoryTest {

  @Autowired
  FeedRepository feedRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  WeatherRepository weatherRepository;

  @Autowired
  EntityManager em;

  @TestConfiguration
  static class QuerydslTestConfig {
    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
      return new JPAQueryFactory(em);
    }
  }

  @Test
  void findFeedListByCursor_ordersByCreatedAtDescAndLimits() {
    User author = userRepository.save(newUser("author@example.com"));
    Weather weather = weatherRepository.save(newWeather(LocalDate.of(2026, 2, 10)));

    Feed f1 = feedRepository.save(Feed.create(author, weather, List.of(), "f1"));
    Feed f2 = feedRepository.save(Feed.create(author, weather, List.of(), "f2"));
    Feed f3 = feedRepository.save(Feed.create(author, weather, List.of(), "f3"));
    em.flush();

    setFeedTimestamps(f1.getId(), LocalDateTime.of(2026, 2, 10, 10, 0));
    setFeedTimestamps(f2.getId(), LocalDateTime.of(2026, 2, 10, 9, 0));
    setFeedTimestamps(f3.getId(), LocalDateTime.of(2026, 2, 10, 8, 0));
    em.flush();
    em.clear();

    FeedDtoCursorRequest request = new FeedDtoCursorRequest(
        null,
        null,
        2,
        FeedSortBy.createdAt,
        FeedSortDirection.DESCENDING,
        null,
        null,
        null,
        null
    );

    List<Feed> result = feedRepository.findFeedListByCursor(request, 2);

    assertThat(result).extracting(Feed::getId)
        .containsExactly(f1.getId(), f2.getId());
  }

  @Test
  void findFeedListByCursor_appliesCursorWhenDescending() {
    User author = userRepository.save(newUser("author2@example.com"));
    Weather weather = weatherRepository.save(newWeather(LocalDate.of(2026, 2, 10)));

    Feed f1 = feedRepository.save(Feed.create(author, weather, List.of(), "f1"));
    Feed f2 = feedRepository.save(Feed.create(author, weather, List.of(), "f2"));
    Feed f3 = feedRepository.save(Feed.create(author, weather, List.of(), "f3"));
    em.flush();

    LocalDateTime t1 = LocalDateTime.of(2026, 2, 10, 10, 0);
    LocalDateTime t2 = LocalDateTime.of(2026, 2, 10, 9, 0);
    LocalDateTime t3 = LocalDateTime.of(2026, 2, 10, 8, 0);
    setFeedTimestamps(f1.getId(), t1);
    setFeedTimestamps(f2.getId(), t2);
    setFeedTimestamps(f3.getId(), t3);
    em.flush();
    em.clear();

    FeedDtoCursorRequest request = new FeedDtoCursorRequest(
        t2.toString(),
        f2.getId(),
        10,
        FeedSortBy.createdAt,
        FeedSortDirection.DESCENDING,
        null,
        null,
        null,
        null
    );

    List<Feed> result = feedRepository.findFeedListByCursor(request, 10);

    assertThat(result).extracting(Feed::getId)
        .containsExactly(f3.getId());
  }

  @Test
  void countFeedList_appliesFilters() {
    User author1 = userRepository.save(newUser("a1@example.com"));
    User author2 = userRepository.save(newUser("a2@example.com"));
    Weather weather = weatherRepository.save(newWeather(LocalDate.of(2026, 2, 10)));

    feedRepository.save(Feed.create(author1, weather, List.of(), "hello world"));
    feedRepository.save(Feed.create(author1, weather, List.of(), "goodbye"));
    feedRepository.save(Feed.create(author2, weather, List.of(), "hello there"));
    em.flush();

    FeedDtoCursorRequest request = new FeedDtoCursorRequest(
        null,
        null,
        10,
        FeedSortBy.createdAt,
        FeedSortDirection.DESCENDING,
        "hello",
        null,
        null,
        author1.getId()
    );

    long count = feedRepository.countFeedList(request);

    assertThat(count).isEqualTo(1L);
  }

  private void setFeedTimestamps(UUID feedId, LocalDateTime createdAt) {
    em.createNativeQuery("update feeds set created_at = ?, updated_at = ? where id = ?")
        .setParameter(1, java.sql.Timestamp.valueOf(createdAt))
        .setParameter(2, java.sql.Timestamp.valueOf(createdAt))
        .setParameter(3, feedId)
        .executeUpdate();
  }

  private User newUser(String email) {
    return new User(
        null,
        email,
        "name",
        Role.USER,
        false,
        LocalDateTime.now(),
        LocalDateTime.now(),
        null,
        "password",
        null,
        null
    );
  }

  private Weather newWeather(LocalDate date) {
    return Weather.builder()
        .date(date)
        .latitude(37.67)
        .longitude(126.8)
        .skyStatus(SkyStatus.CLEAR)
        .precipitationType(PrecipitationType.NONE)
        .precipitationAmount(0.0)
        .precipitationProbability(0.0)
        .tempCurrent(10.0)
        .tempMin(5.0)
        .tempMax(12.0)
        .humidity(50.0)
        .windSpeed(2.0)
        .windStrength(WindStrength.WEAK)
        .forecastAt(date.atTime(12, 0))
        .createdAt(LocalDateTime.now())
        .build();
  }
}
