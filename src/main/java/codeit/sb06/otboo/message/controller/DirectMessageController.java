package codeit.sb06.otboo.message.controller;

import codeit.sb06.otboo.message.dto.response.DirectMessageDtoCursorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/direct-messages")
public class DirectMessageController {

    @GetMapping
    public ResponseEntity<DirectMessageDtoCursorResponse> getDirectMessages(
            @RequestParam UUID userId,
            @RequestParam String cursor,
            @RequestParam UUID idAfter,
            @RequestParam int limit
    ) {
        return ResponseEntity.ok().build();
    }
}
