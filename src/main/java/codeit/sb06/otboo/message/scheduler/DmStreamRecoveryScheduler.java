package codeit.sb06.otboo.message.scheduler;

import codeit.sb06.otboo.common.scheduler.AbstractStreamRecoveryScheduler;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class DmStreamRecoveryScheduler extends AbstractStreamRecoveryScheduler {

    private final SimpMessagingTemplate messagingTemplate;
    private final String dmStreamKey;

    public DmStreamRecoveryScheduler(
            StringRedisTemplate redisTemplate,
            CircuitBreakerRegistry circuitBreakerRegistry,
            String serverId,
            String dmStreamKey,
            SimpMessagingTemplate messagingTemplate) {

        super(redisTemplate, circuitBreakerRegistry, serverId);
        this.dmStreamKey = dmStreamKey;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    protected String getStreamKey() {
        return dmStreamKey;
    }

    @Override
    protected String getCircuitBreakerName() {
        return "dmStreamCircuit";
    }

    @Override
    protected String getStreamNameForLog() {
        return "DM";
    }

    @Override
    protected Duration getMinIdleTime() {
        return Duration.ofSeconds(10);
    }

    @Override
    protected String getGroupNamePrefix() {
        return "group-dm-";
    }

    @Override
    protected String getWorkerNamePrefix() {
        return "dm-recover-worker-";
    }

    @Override
    protected void processClaimedRecord(MapRecord<String, String, String> record) {
        String json = record.getValue().get("payload");
        String destination = record.getValue().get("destination");

        messagingTemplate.convertAndSend(destination, json);
    }

    @Scheduled(fixedDelay = 3000, initialDelayString = "${scheduler.initial-delay.dm}")
    @CircuitBreaker(name = "dmStreamCircuit", fallbackMethod = "fallbackRecover")
    public void recoverDmMessages() {
        super.doRecover();
    }

    public void fallbackRecover(Exception e) {
        super.doFallbackRecover(e);
    }
}
