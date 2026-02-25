package codeit.sb06.otboo.common.scheduler;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import static io.github.resilience4j.circuitbreaker.CircuitBreaker.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractStreamRecoveryScheduler {

    protected final StringRedisTemplate redisTemplate;
    protected final CircuitBreakerRegistry circuitBreakerRegistry;
    protected final String serverId;
    protected String groupName;

    // 자식 클래스들이 구현해야 할 설정값들
    protected abstract String getStreamKey();
    protected abstract String getCircuitBreakerName();
    protected abstract String getStreamNameForLog();
    protected abstract Duration getMinIdleTime();
    protected abstract String getGroupNamePrefix();
    protected abstract String getWorkerNamePrefix();

    protected abstract void processClaimedRecord(MapRecord<String, String, String> record) throws Exception;

    @PostConstruct
    public void init() {
        this.groupName = getGroupNamePrefix() + serverId;

        circuitBreakerRegistry.circuitBreaker(getCircuitBreakerName())
                .getEventPublisher()
                .onStateTransition(event -> {
                    StateTransition transition = event.getStateTransition();
                    if (transition.getToState() == State.OPEN) {
                        log.error("{} 스트림 회로 차단됨 (OPEN), 시각: {}", getStreamNameForLog(), event.getCreationTime());
                        // 관리자 이메일에 알림 전송
                    } else if (transition.getToState() == State.CLOSED &&
                            transition.getFromState() == State.HALF_OPEN) {
                        log.info("{} 스트림 회로 복구됨 (CLOSED), 시각: {}", getStreamNameForLog(), event.getCreationTime());
                        // 관리자 이메일에 알림 전송
                    }
                });
    }

    protected void doRecover() {
        // id 범위는 unbounded로 설정하여 모든 대기 메시지를 조회
        // 최대 100개까지 조회하여 처리
        PendingMessages pendingMessages = redisTemplate.opsForStream()
                .pending(getStreamKey(), groupName, Range.unbounded(), 100L);

        for (PendingMessage msg : pendingMessages) {
            processPendingMessage(msg);
        }
    }

    private void processPendingMessage(PendingMessage msg) {
        if (msg.getElapsedTimeSinceLastDelivery().toMillis() >= getMinIdleTime().toMillis()) {
            if (msg.getTotalDeliveryCount() > 5) {
                log.warn("{} 메시지 {}가 {}회 이상 재전송 실패하여 ACK 처리", getStreamNameForLog(), msg.getId(), msg.getTotalDeliveryCount());
                redisTemplate.opsForStream().acknowledge(getStreamKey(), groupName, msg.getId());
                return;
            }
            // 소유권 가져오기 (XCLAIM)
            StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
            List<MapRecord<String, String, String>> claimed = streamOps.claim(
                    getStreamKey(),
                    groupName,
                    getWorkerNamePrefix() + serverId,
                    RedisStreamCommands.XClaimOptions.minIdle(getMinIdleTime()).ids(msg.getId())
            );

            for (MapRecord<String, String, String> record : claimed) {
                try {
                    // 자식에게 전송 로직 위임
                    processClaimedRecord(record);

                    redisTemplate.opsForStream().acknowledge(getStreamKey(), groupName, record.getId());
                    log.debug("{} 복구 및 ACK 완료: [MessageId: {}]", getStreamNameForLog(), record.getId());
                } catch (Exception e) {
                    log.error("{} 복구 실패: {}, 오류: {}", getStreamNameForLog(), record.getId(), e.getMessage());
                }
            }
        }
    }

    protected void doFallbackRecover(Exception e) {
        log.trace(">>>> [CIRCUIT OPEN] Redis 장애로 {} recovery 스케줄러 일시 정지 상태: {}", getStreamNameForLog(), e.getMessage());
    }
}
