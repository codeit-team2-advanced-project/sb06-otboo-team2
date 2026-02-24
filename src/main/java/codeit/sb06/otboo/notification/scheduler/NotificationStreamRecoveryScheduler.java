package codeit.sb06.otboo.notification.scheduler;

import codeit.sb06.otboo.notification.service.SseService;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationStreamRecoveryScheduler {

    public static final Duration MIN_IDLE_TIME = Duration.ofMinutes(1);
    private final StringRedisTemplate redisTemplate;
    private final SseService sseService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final String notificationStreamKey;
    private final String serverId;
    private String groupName;

    @PostConstruct
    public void init() {
        groupName = "group-noti-" + serverId;

        circuitBreakerRegistry.circuitBreaker("notificationStreamCircuit")
                .getEventPublisher()
                .onStateTransition(event -> {
                    StateTransition transition = event.getStateTransition();
                    // CLOSED -> OPEN, HALF_OPEN -> OPEN
                    if (transition.getToState() == State.OPEN) {
                        log.debug("알림 스트림 회로 차단됨 (OPEN), 시각: {}", event.getCreationTime());
                        // 관리자 이메일에 알림 전송
                    }
                    // OPEN -> HALF_OPEN -> CLOSED
                    else if (transition.getToState() == State.CLOSED &&
                            transition.getFromState() == State.HALF_OPEN) {
                        log.debug("알림 스트림 회로 복구됨 (CLOSED), 시각: {}", event.getCreationTime());
                        // 관리자 이메일에 알림 전송
                    }
                });
    }

    @Scheduled(fixedDelay = 20000, initialDelayString = "${scheduler.initial-delay.noti}")
    @CircuitBreaker(name = "notificationStreamCircuit", fallbackMethod = "fallbackRecover")
    public void recoverNotificationMessages() {

        PendingMessages pendingMessages = redisTemplate.opsForStream()
                .pending(notificationStreamKey, groupName, Range.unbounded(), 100L);

        for (PendingMessage msg : pendingMessages) {
            processPendingMessage(msg);
        }
    }

    private void processPendingMessage(PendingMessage msg) {
        if (msg.getElapsedTimeSinceLastDelivery().toMillis() >= MIN_IDLE_TIME.toMillis()) {
            if (msg.getTotalDeliveryCount() > 5) {
                log.warn("알림 {}가 {}회 이상 재전송 실패하여 ACK 처리", msg.getId(), msg.getTotalDeliveryCount());
                redisTemplate.opsForStream().acknowledge(notificationStreamKey, groupName, msg.getId());
                return;
            }

            StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
            List<MapRecord<String, String, String>> claimed = streamOps.claim(
                    notificationStreamKey,
                    groupName,
                    "notification-recover-worker-" + serverId,
                    RedisStreamCommands.XClaimOptions.minIdle(MIN_IDLE_TIME).ids(msg.getId())
            );

            for (MapRecord<String, String, String> record : claimed) {
                try {
                    String json = record.getValue().get("payload");
                    String receiverId = record.getValue().get("receiverId");

                    sseService.send(UUID.fromString(receiverId), "notifications", json);

                    redisTemplate.opsForStream().acknowledge(notificationStreamKey, "group-noti-" + serverId, record.getId());
                    log.debug("알림 복구 및 ACK 완료: [MessageId: {}, ReceiverId: {}]", record.getId(), receiverId);
                } catch (Exception e) {
                    log.error("알림 복구 실패: {}, 오류: {}", record.getId(), e.getMessage());
                }
            }
        }
    }

    public void fallbackRecover(Exception e) {
        log.trace(">>>> [CIRCUIT OPEN] Redis 장애로 알림 recovery 스케줄러 일시 정지 상태: {}", e.getMessage());
    }
}
