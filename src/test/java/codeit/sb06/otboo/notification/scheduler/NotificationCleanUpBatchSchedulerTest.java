package codeit.sb06.otboo.notification.scheduler;

import codeit.sb06.otboo.exception.notification.NotificationBatchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationCleanUpBatchSchedulerTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job deleteOldNotificationsJob;

    @InjectMocks
    private NotificationCleanUpBatchScheduler scheduler;

    @Test
    @DisplayName("성공: Job이 정상적으로 실행되는지 확인한다")
    void runDailyJob_Success() throws Exception {
        // when
        scheduler.runDailyJob();

        // then
        verify(jobLauncher).run(eq(deleteOldNotificationsJob), any(JobParameters.class));
    }

    @Test
    @DisplayName("실패: Job 실행 중 예외가 발생하면 NotificationBatchException이 던져지는지 확인한다")
    void runDailyJob_ThrowsException() throws Exception {
        // given
        given(jobLauncher.run(eq(deleteOldNotificationsJob), any(JobParameters.class)))
                .willThrow(new RuntimeException("CleanUp Batch Execution Failed"));

        // when & then
        assertThrows(NotificationBatchException.class, () -> scheduler.runDailyJob());
    }
}
