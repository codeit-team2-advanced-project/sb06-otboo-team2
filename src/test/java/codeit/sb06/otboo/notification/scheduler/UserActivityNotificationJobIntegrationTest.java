package codeit.sb06.otboo.notification.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserActivityNotificationJobIntegrationTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job userActivityStatNotificationJob;

    @Test
    @DisplayName("유저 활동 통계 알림 Job 정상 실행 통합 테스트")
    void userActivityStatNotificationJob_Success() throws Exception {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        JobParameters params = new JobParametersBuilder()
                .addLocalDateTime("startDate", oneWeekAgo)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // when
        JobExecution execution = jobLauncher.run(userActivityStatNotificationJob, params);

        // then
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }
}
