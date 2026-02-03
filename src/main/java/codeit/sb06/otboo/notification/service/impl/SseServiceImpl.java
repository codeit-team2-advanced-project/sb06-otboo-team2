package codeit.sb06.otboo.notification.service.impl;

import codeit.sb06.otboo.notification.repository.SseEmitterRepository;
import codeit.sb06.otboo.notification.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SseServiceImpl implements SseService {

    public static final long TIMEOUT = 60 * 1000L * 60;
    private final SseEmitterRepository sseEmitterRepository;

    @Override
    public SseEmitter subscribe(UUID userId, UUID lastEventId) {

        SseEmitter emitter = new SseEmitter(TIMEOUT); // 1 hour

        emitter.onCompletion(() -> sseEmitterRepository.deleteById(userId));
        emitter.onTimeout(() -> sseEmitterRepository.deleteById(userId));
        emitter.onError((e) -> sseEmitterRepository.deleteById(userId));

        sendToClient(emitter, userId, "subscribe" , "EventStream Created. [userId=" + userId + "]");

        sseEmitterRepository.save(userId, emitter);

        return emitter;
    }

    @Override
    public void send(UUID userId, String eventName, Object data) {
        SseEmitter emitter = sseEmitterRepository.findById(userId);
        if (emitter != null) {
            sendToClient(emitter, userId, eventName, data);
        }
    }

    private void sendToClient(SseEmitter emitter, UUID userId, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(UUID.randomUUID().toString())
                    .name(eventName)
                    .data(data));
        } catch (Exception e) {
            sseEmitterRepository.deleteById(userId);
        }
    }

    private String makeTimeIncludeId(UUID userId) {
        return userId.toString() + "_" + System.currentTimeMillis();
    }
}
