package codeit.sb06.otboo.notification.batch;

import codeit.sb06.otboo.notification.entity.Notification;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class NotificationCleanupBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job deleteOldNotificationsJob() {
        return new JobBuilder("deleteOldNotificationsJob", jobRepository)
                .start(deleteOldNotificationsStep())
                .build();
    }

    @Bean
    public Step deleteOldNotificationsStep() {
        return new StepBuilder("deleteOldNotificationsStep", jobRepository)
                .<Notification, Notification>chunk(100, transactionManager)
                .reader(expiredNotificationReader())
                .writer(expiredNotificationWriter())
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(3)
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<Notification> expiredNotificationReader() {

        LocalDateTime date = LocalDateTime.now().minusDays(30);

        return new JpaCursorItemReaderBuilder<Notification>()
                .name("expiredNotificationReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT n FROM Notification n WHERE n.createdAt < :date ORDER BY n.id ASC")
                .parameterValues(Map.of("date", date))
                .build();
    }

    @Bean
    public JpaItemWriter<Notification> expiredNotificationWriter() {
        return new JpaItemWriterBuilder<Notification>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
