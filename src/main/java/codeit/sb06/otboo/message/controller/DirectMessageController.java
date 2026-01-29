package codeit.sb06.otboo.message.controller;

import codeit.sb06.otboo.message.dto.response.DirectMessageDtoCursorResponse;
import codeit.sb06.otboo.message.service.DirectMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/direct-messages")
@RequiredArgsConstructor
public class DirectMessageController {

    private final DirectMessageService directMessageService;

    @GetMapping
    public ResponseEntity<DirectMessageDtoCursorResponse> getDirectMessages(
            @RequestParam(name = "userId") UUID senderId,
            @RequestParam(required = false) LocalDateTime cursor,
            @RequestParam(required = false) UUID idAfter,
            @RequestParam int limit,
            @RequestParam UUID myUserId             // 테스트용
//            @AuthenticationPrincipal(expression = "id") UUID myUserId
    ) {
        DirectMessageDtoCursorResponse response = directMessageService.getDirectMessages(myUserId, senderId, cursor, idAfter, limit);
        return ResponseEntity.ok().body(response);
    }
}
