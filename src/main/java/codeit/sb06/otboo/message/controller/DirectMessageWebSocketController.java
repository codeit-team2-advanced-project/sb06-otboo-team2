package codeit.sb06.otboo.message.controller;

import codeit.sb06.otboo.message.dto.request.DirectMessageCreateRequest;
import codeit.sb06.otboo.message.service.DirectMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DirectMessageWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final DirectMessageService directMessageService;

    @MessageMapping("/direct-messages_send")
    public void sendDirectMessage(
            @Payload DirectMessageCreateRequest request
    ) {

        log.debug("웹소켓 직접 메시지 전송 요청 받음: {}", request);

        List<UUID> ids = Arrays.asList(request.senderId(), request.receiverId());
        Collections.sort(ids);
        String destination = "/sub/direct-messages_" + ids.get(0) + "_" + ids.get(1);

        directMessageService.create(request);
        messagingTemplate.convertAndSend(destination, request);
    }
}
