package codeit.sb06.otboo.notification.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

public interface SseService {

    SseEmitter subscribe(UUID userId, UUID lastEventId);

    void send(UUID userId, String eventName, Object data);
}
