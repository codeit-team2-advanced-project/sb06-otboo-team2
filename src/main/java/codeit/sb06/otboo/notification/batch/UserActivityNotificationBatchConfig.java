package codeit.sb06.otboo.notification.batch;

import codeit.sb06.otboo.notification.dto.NotificationDto;
import codeit.sb06.otboo.notification.dto.StatNotificationDTO;
import codeit.sb06.otboo.notification.entity.Notification;
import codeit.sb06.otboo.notification.enums.NotificationLevel;
import codeit.sb06.otboo.notification.mapper.NotificationMapper;
import codeit.sb06.otboo.notification.publisher.RedisNotificationPublisher;
import codeit.sb06.otboo.notification.repository.NotificationRepository;
import codeit.sb06.otboo.notification.service.NotificationCacheService;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class UserActivityNotificationBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final NotificationMapper notificationMapper;
    private final NotificationCacheService notificationCacheService;
    private final RedisNotificationPublisher redisNotificationPublisher;
    private final NotificationRepository notificationRepository;

    @Bean
    public Job userActivityStatNotificationJob() {
        return new JobBuilder("userActivityStatNotificationJob", jobRepository)
                .start(collectActivityStatStep())
                .next(sendStatNotificationStep())
                .build();
    }

    @Bean
    public Step collectActivityStatStep() {
        return new StepBuilder("collectActivityStatStep", jobRepository)
                .<StatNotificationDTO, Notification>chunk(100, transactionManager)
                .reader(activityStatReader(null))
                .processor(activityStatProcessor())
                .writer(activityStatWriter())
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(3)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<StatNotificationDTO> activityStatReader(
            @Value("#{jobParameters['startDate']}") LocalDateTime startDate
    ) {

        return new JpaPagingItemReaderBuilder<StatNotificationDTO>()
                .name("activityStatReader")
                .entityManagerFactory(entityManagerFactory)
                /*
                 * 여러 개의 1:N 관계를 한꺼번에 JOIN 하면 데이터가 폭발적으로 증가할 수 있음
                 * LEFT JOIN - 유저 1 * 피드 20 * 좋아요 30 * 댓글 40 -> 24,000행 발생
                 * 서브쿼리 - 유저 1, 피드 20, 좋아요 30, 댓글 40 -> 1행 발생
                 */
                .queryString("""
                        SELECT new codeit.sb06.otboo.notification.dto.StatNotificationDTO(
                            u.id,
                            (SELECT COUNT(f) FROM Feed f WHERE f.user.id = u.id AND f.createdAt >= :startDate),
                            (SELECT COUNT(fl) FROM FeedLike fl WHERE fl.user.id = u.id AND fl.createdAt >= :startDate),
                            (SELECT COUNT(c) FROM Comment c WHERE c.user.id = u.id AND c.createdAt >= :startDate)
                        )
                        FROM User u
                        WHERE EXISTS (SELECT 1 FROM Feed f WHERE f.user.id = u.id AND f.createdAt >= :startDate)
                           OR EXISTS (SELECT 1 FROM FeedLike fl WHERE fl.user.id = u.id AND fl.createdAt >= :startDate)
                            OR EXISTS (SELECT 1 FROM Comment c WHERE c.user.id = u.id AND c.createdAt >= :startDate)
                        ORDER BY u.id ASC
                        """)
                .pageSize(100)
                .parameterValues(Map.of("startDate", startDate))
                .build();
    }

    @Bean
    public ItemProcessor<StatNotificationDTO, Notification> activityStatProcessor() {

        return dto -> Notification.builder()
                .receiverId(dto.receiverId())
                .title("주간 활동 리포트")
                .content(String.format("이번 주에 작성한 피드: %d개, 받은 좋아요: %d개, 받은 댓글: %d개",
                        dto.feedCount(), dto.feedLikeCount(), dto.feedCommentCount()))
                .level(NotificationLevel.PENDING)
                .build();
    }

    @Bean
    public JpaItemWriter<Notification> activityStatWriter() {
        return new JpaItemWriterBuilder<Notification>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Step sendStatNotificationStep() {
        return new StepBuilder("sendStatNotificationStep", jobRepository)
                .<Notification, Notification>chunk(100, transactionManager)
                .reader(pendingNotificationReader())
                .writer(updateNotificationLevelWriter())
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(3)
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<Notification> pendingNotificationReader() {
        return new JpaCursorItemReaderBuilder<Notification>()
                .name("pendingNotificationReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT n FROM Notification n WHERE n.level = :level")
                .parameterValues(Map.of("level", NotificationLevel.PENDING))
                .build();
    }

    @Bean
    public ItemWriter<Notification> updateNotificationLevelWriter() {
        return chunk -> {

            List<NotificationDto> dtoList = chunk.getItems().stream()
                    .map(notificationMapper::toDto)
                    .toList();
            notificationCacheService.saveAll(dtoList);
            redisNotificationPublisher.publishAll(dtoList);

            for (Notification notification : chunk) {
                notification.setLevel(NotificationLevel.INFO);
            }

            notificationRepository.saveAll(chunk.getItems());
        };
    }
}
