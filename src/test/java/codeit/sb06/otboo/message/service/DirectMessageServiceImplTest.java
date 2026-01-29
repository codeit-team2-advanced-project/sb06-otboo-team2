package codeit.sb06.otboo.message.service;

import codeit.sb06.otboo.message.dto.DirectMessageDto;
import codeit.sb06.otboo.message.dto.request.DirectMessageCreateRequest;
import codeit.sb06.otboo.message.entity.ChatRoom;
import codeit.sb06.otboo.message.entity.DirectMessage;
import codeit.sb06.otboo.message.mapper.DirectMessageMapper;
import codeit.sb06.otboo.message.repository.DirectMessageRepository;
import codeit.sb06.otboo.message.service.impl.DirectMessageServiceImpl;
import codeit.sb06.otboo.user.entity.Users;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.util.EasyRandomUtil;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DirectMessageServiceImplTest {

    private final EasyRandom easyRandom = EasyRandomUtil.getRandom();

    @Mock
    private DirectMessageRepository directMessageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRoomService chatRoomService;

    // 실제 변환을 위해 spy 사용
    @Spy
    private DirectMessageMapper directMessageMapper;

    @InjectMocks
    private DirectMessageServiceImpl directMessageService;

    @Test
    @DisplayName("Direct Message 생성 테스트를 성공한다.")
    void createDirectMessageTest() {
        // given
        DirectMessageCreateRequest request = easyRandom.nextObject(DirectMessageCreateRequest.class);

        given(userRepository.findById(request.senderId()))
                .willReturn(Optional.of(mock(Users.class)));
        given(userRepository.findById(request.receiverId()))
                .willReturn(Optional.of(mock(Users.class)));
        given(chatRoomService.getOrCreatePrivateRoom(request.senderId(), request.receiverId()))
                .willReturn(mock(ChatRoom.class));
        given(directMessageRepository.save(any(DirectMessage.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        DirectMessageDto dmDto = directMessageService.create(request);

        // then
        assertAll(
                () -> assertThat(dmDto).isNotNull(),
                () -> assertThat(dmDto.content()).isEqualTo(request.content())
        );
    }
}
