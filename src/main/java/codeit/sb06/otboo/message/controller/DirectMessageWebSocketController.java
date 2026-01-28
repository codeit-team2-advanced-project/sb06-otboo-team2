package codeit.sb06.otboo.message.controller;

import codeit.sb06.otboo.message.dto.request.DirectMessageCreateRequest;
import codeit.sb06.otboo.message.entity.ChatRoom;
import codeit.sb06.otboo.message.service.DirectMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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

        String dmKey = ChatRoom.generateDmKey(request.senderId(), request.receiverId());
        String destination = "/sub/direct-messages_" + dmKey;

        directMessageService.create(request);
        messagingTemplate.convertAndSend(destination, request);
    }
}
