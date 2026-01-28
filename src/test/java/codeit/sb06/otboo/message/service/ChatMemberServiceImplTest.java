package codeit.sb06.otboo.message.service;


import codeit.sb06.otboo.message.entity.ChatMember;
import codeit.sb06.otboo.message.entity.ChatRoom;
import codeit.sb06.otboo.message.repository.ChatMemberRepository;
import codeit.sb06.otboo.message.service.impl.ChatMemberServiceImpl;
import codeit.sb06.otboo.user.entity.Users;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.util.EasyRandomUtil;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMemberServiceImplTest {

    private final EasyRandom easyRandom = EasyRandomUtil.getRandom();
    @Mock
    private ChatMemberRepository chatMemberRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ChatMemberServiceImpl chatMemberService;

    @Test
    @DisplayName("채팅 멤버를 생성하고 반환한다.")
    void createChatMemberTest() {
        //given
        ChatRoom chatRoom = mock(ChatRoom.class);
        Users user = easyRandom.nextObject(Users.class);
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

        given(userRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(user));
        given(chatMemberRepository.save(any(ChatMember.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        ChatMember createdChatMember = chatMemberService.create(chatRoom, user.getId());

        // then
        assertAll(
                () -> assertThat(createdChatMember.getUser()).isEqualTo(user),
                () -> assertThat(createdChatMember.getChatRoom()).isEqualTo(chatRoom),
                () -> verify(chatMemberRepository, times(1)).save(any())
        );
    }

    @Test
    @DisplayName("존재하지 않는 유저로 채팅 멤버 생성 시도 시 예외가 발생한다.")
    void createChatMemberWithNonExistentUserTest() {
        //given
        UUID invalidUserId = UUID.randomUUID();

        given(userRepository.findById(invalidUserId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> chatMemberService.create(mock(ChatRoom.class), invalidUserId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(invalidUserId.toString());
        verify(chatMemberRepository, never()).save(any());
    }
}
