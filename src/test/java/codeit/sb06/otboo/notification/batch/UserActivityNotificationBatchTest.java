package codeit.sb06.otboo.notification.batch;

import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.feed.repository.FeedRepository;
import codeit.sb06.otboo.notification.config.EmbeddedRedisConfig;
import codeit.sb06.otboo.notification.entity.Notification;
import codeit.sb06.otboo.notification.enums.NotificationLevel;
import codeit.sb06.otboo.notification.repository.NotificationRepository;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.util.EasyRandomUtil;
import codeit.sb06.otboo.weather.entity.Weather;
import codeit.sb06.otboo.weather.repository.WeatherRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@Import(EmbeddedRedisConfig.class)
class UserActivityNotificationBatchTest {

    private final EasyRandom easyRandom = EasyRandomUtil.getRandom();

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private WeatherRepository weatherRepository;

    @MockitoBean
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> container;

    @Autowired
    private Job userActivityStatNotificationJob;

    @BeforeEach
    void setup() {
        jobLauncherTestUtils.setJob(userActivityStatNotificationJob);
    }

    @AfterEach
    void cleanUp() {
        jobRepositoryTestUtils.removeJobExecutions();
        notificationRepository.deleteAllInBatch();
        feedRepository.deleteAllInBatch();
        weatherRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자 활동 알림 배치가 성공적으로 실행된다.")
    void userActivityJobTest() throws Exception {
        // given
        User user = easyRandom.nextObject(User.class);
        userRepository.save(user);

        Weather weather = easyRandom.nextObject(Weather.class);
        weatherRepository.save(weather);

        Feed feed = easyRandom.nextObject(Feed.class);
        ReflectionTestUtils.setField(feed, "user", user);
        ReflectionTestUtils.setField(feed, "weather", weather);
        ReflectionTestUtils.setField(feed, "createdAt", LocalDateTime.of(2026, 2, 5, 0, 0));
        feedRepository.save(feed);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("startDate", "2026-02-01T00:00:00")
                .addLong("runTime", System.currentTimeMillis())
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        jobExecution.getStepExecutions().forEach(step -> {
            assertThat(step.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        });

        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).isNotEmpty();
        assertThat(notifications.get(0).getLevel()).isEqualTo(NotificationLevel.INFO);
    }
}
