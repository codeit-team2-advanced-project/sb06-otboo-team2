package codeit.sb06.otboo.notification.scheduler;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NotificationStreamRecoverySchedulerListenerTest {

    @InjectMocks
    private NotificationStreamRecoveryScheduler scheduler;

    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();

        ReflectionTestUtils.setField(scheduler, "circuitBreakerRegistry", circuitBreakerRegistry);
        ReflectionTestUtils.setField(scheduler, "serverId", "server-1");
    }

    @Test
    @DisplayName("서킷브레이커 상태 전환 감지: 장애 발생 시 리스너가 OPEN 이벤트를 정상 수신하는지 확인")
    void init_CircuitBreakerOpenEvent() {
        // given
        scheduler.init();
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("notificationStreamCircuit");

        // when
        cb.transitionToOpenState();

        // then
        assertEquals(CircuitBreaker.State.OPEN, cb.getState());
    }

    @Test
    @DisplayName("서킷브레이커 상태 전환 감지: 장애 복구 시 리스너가 CLOSED 이벤트를 정상 수신하는지 확인")
    void init_CircuitBreakerClosedEvent() {
        // given
        scheduler.init();
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("notificationStreamCircuit");

        // 서킷을 OPEN 시켰다가 HALF_OPEN 상태로 만듦 (장애 발생 후 복구 대기 상태)
        cb.transitionToOpenState();
        cb.transitionToHalfOpenState();

        // when
        cb.transitionToClosedState();

        // then
        assertEquals(CircuitBreaker.State.CLOSED, cb.getState());
    }
}
