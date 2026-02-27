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
class UserActivityNotificationBatchSchedulerTest {

    @Mock
    private JobLauncher jobLauncher;
    @Mock
    private Job userActivityStatNotificationJob;

    @InjectMocks
    private UserActivityNotificationBatchScheduler scheduler;

    @Test
    @DisplayName("성공적으로 배치 실행: JobLauncher가 정상적으로 호출되는지 확인")
    void runDailyJob_Success() throws Exception {
        // when
        scheduler.runWeeklyJob();

        // then
        verify(jobLauncher).run(eq(userActivityStatNotificationJob), any(JobParameters.class));
    }

    @Test
    @DisplayName("배치 실행 실패 시 예외 처리: JobLauncher가 예외를 던질 때 NotificationBatchException이 발생하는지 확인")
    void runDailyJob_ThrowsException() throws Exception {
        // given
        given(jobLauncher.run(eq(userActivityStatNotificationJob), any(JobParameters.class)))
                .willThrow(new RuntimeException("Batch Execution Failed"));

        // when & then
        assertThrows(NotificationBatchException.class, () -> scheduler.runWeeklyJob());
    }
}
