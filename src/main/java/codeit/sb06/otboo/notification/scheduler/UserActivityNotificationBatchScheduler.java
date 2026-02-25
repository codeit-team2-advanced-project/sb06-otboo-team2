package codeit.sb06.otboo.notification.scheduler;

import codeit.sb06.otboo.exception.notification.NotificationBatchException;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class UserActivityNotificationBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job userActivityStatNotificationJob;

    @Scheduled(cron = "0 00 1 * * MON")
    @SchedulerLock(name = "UserActivityNotificationBatchSchedulerLock", lockAtMostFor = "PT1H", lockAtLeastFor = "PT10M")
    public void runWeeklyJob() {

        try {
            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("startDate", LocalDateTime.now().minusWeeks(1))
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(userActivityStatNotificationJob, params);
        } catch (Exception e) {
            throw new NotificationBatchException(e);
        }
    }
}
