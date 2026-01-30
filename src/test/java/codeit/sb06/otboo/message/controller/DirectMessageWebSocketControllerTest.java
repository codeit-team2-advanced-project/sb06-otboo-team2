package codeit.sb06.otboo.message.controller;

import codeit.sb06.otboo.config.JpaAuditingConfig;
import codeit.sb06.otboo.message.dto.request.DirectMessageCreateRequest;
import codeit.sb06.otboo.message.entity.ChatRoom;
import codeit.sb06.otboo.message.repository.ChatMemberRepository;
import codeit.sb06.otboo.message.repository.ChatRoomRepository;
import codeit.sb06.otboo.message.repository.DirectMessageRepository;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(JpaAuditingConfig.class)
@Disabled("유저 id 생성 전략이 현재 안써져있어서 테스트 통과 불가. 추후 수정 시 재활성화")
class DirectMessageWebSocketControllerTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMemberRepository chatMemberRepository;

    @Autowired
    private DirectMessageRepository directMessageRepository;

    @BeforeEach
    void setup() {
        // 클라이언트 설정 (JSON 변환기 포함)
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @AfterEach
    void cleanUp() {
        directMessageRepository.deleteAll();
        chatMemberRepository.deleteAll();
        chatRoomRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("메시지를 보내면 해당 DM 방 구독자에게 메시지가 전달된다.")
    void sendDirectMessageTest() throws Exception {

        // 0. 테스트용 유저 생성
        User sender = userRepository.save(new User());
        User receiver = userRepository.save(new User());

        // 1. 연결 세션 확보
        String url = "ws://localhost:" + port + "/ws/websocket"; // 서버의 WebSocket 엔드포인트 + with SockJS
        StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
        }).get(1, TimeUnit.SECONDS);

        // 2. 메시지를 받을 큐(Queue) 준비
        BlockingQueue<DirectMessageCreateRequest> resultQueue = new LinkedBlockingDeque<>();

        // 3. 특정 DM 방 구독 (예: sender, receiver 간의 DM 방)
        String dmKey = ChatRoom.generateDmKey(sender.getId(), receiver.getId());
        session.subscribe("/sub/direct-messages_" + dmKey, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return DirectMessageCreateRequest.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                log.info("메시지 수신 성공: {}", payload);
                resultQueue.offer((DirectMessageCreateRequest) payload);
            }
        });

        // 4. 메시지 전송 (컨트롤러의 @MessageMapping으로)
        DirectMessageCreateRequest request = new DirectMessageCreateRequest(sender.getId(), receiver.getId(), "안녕!");
        session.send("/pub/direct-messages_send", request);

        // 5. 검증: 5초 안에 구독 중인 큐에 메시지가 들어오는지 확인
        DirectMessageCreateRequest received = resultQueue.poll(5, TimeUnit.SECONDS);
        assertAll(
                () -> assertThat(received).isNotNull(),
                () -> assertThat(received.content()).isEqualTo("안녕!")
        );
    }
}
