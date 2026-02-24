package codeit.sb06.otboo.message.scheduler;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import static io.github.resilience4j.circuitbreaker.CircuitBreaker.*;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DmStreamRecoveryScheduler {

    public static final Duration MIN_IDLE_TIME = Duration.ofSeconds(10);
    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final String dmStreamKey;
    private final String serverId;
    private String groupName;

    @PostConstruct
    public void init() {
        groupName = "group-dm-" + serverId;

        circuitBreakerRegistry.circuitBreaker("dmStreamCircuit")
                .getEventPublisher()
                .onStateTransition(event -> {
                    StateTransition transition = event.getStateTransition();
                    // CLOSED -> OPEN, HALF_OPEN -> OPEN
                    if (transition.getToState() == State.OPEN) {
                        log.debug("DM 스트림 회로 차단됨 (OPEN), 시각: {}", event.getCreationTime());
                        // 관리자 이메일에 알림 전송
                    }
                    // OPEN -> HALF_OPEN -> CLOSED
                    else if (transition.getToState() == State.CLOSED &&
                            transition.getFromState() == State.HALF_OPEN) {
                        log.debug("DM 스트림 회로 복구됨 (CLOSED), 시각: {}", event.getCreationTime());
                        // 관리자 이메일에 알림 전송
                    }
                });
    }

    @Scheduled(fixedDelay = 10000, initialDelayString = "${scheduler.initial-delay.dm}")
    @CircuitBreaker(name = "dmStreamCircuit", fallbackMethod = "fallbackRecover")
    public void recoverDmMessages() {
        // id 범위는 unbounded로 설정하여 모든 대기 메시지를 조회
        // 최대 100개까지 조회하여 처리
        PendingMessages pendingMessages = redisTemplate.opsForStream()
                .pending(dmStreamKey, groupName, Range.unbounded(), 100L);

        for (PendingMessage msg : pendingMessages) {
            processPendingMessage(msg);
        }
    }

    private void processPendingMessage(PendingMessage msg) {
        if (msg.getElapsedTimeSinceLastDelivery().toMillis() >= MIN_IDLE_TIME.toMillis()) {
            if (msg.getTotalDeliveryCount() > 5) {
                log.warn("DM 메시지 {}가 {}회 이상 재전송 실패하여 ACK 처리", msg.getId(), msg.getTotalDeliveryCount());
                redisTemplate.opsForStream().acknowledge(dmStreamKey, groupName, msg.getId());
                return;
            }

            // 소유권 가져오기 (XCLAIM)
            StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
            List<MapRecord<String, String, String>> claimed = streamOps.claim(
                    dmStreamKey,
                    groupName,
                    "dm-recover-worker-" + serverId,
                    RedisStreamCommands.XClaimOptions.minIdle(MIN_IDLE_TIME).ids(msg.getId())
            );

            for (MapRecord<String, String, String> record : claimed) {
                try {
                    String json = record.getValue().get("payload");
                    String destination = record.getValue().get("destination");
                    String receiverId = record.getValue().get("receiverId");

                    messagingTemplate.convertAndSend(destination, json);
                    redisTemplate.opsForStream().acknowledge(dmStreamKey, groupName, record.getId());
                    log.debug("DM 복구 및 ACK 완료: [MessageId: {}, ReceiverId: {}]", record.getId(), receiverId);
                } catch (Exception e) {
                    log.error("DM 복구 실패: {}, 오류: {}", record.getId(), e.getMessage());
                }
            }
        }
    }

    public void fallbackRecover(Exception e) {
        log.trace(">>>> [CIRCUIT OPEN] Redis 장애로 dm recovery 스케줄러 일시 정지 상태: {}", e.getMessage());
    }
}
