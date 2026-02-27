package codeit.sb06.otboo.message.service.impl;

import codeit.sb06.otboo.exception.user.UserNotFoundException;
import codeit.sb06.otboo.message.entity.ChatMember;
import codeit.sb06.otboo.message.entity.ChatRoom;
import codeit.sb06.otboo.message.repository.ChatMemberRepository;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.message.service.ChatMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMemberServiceImpl implements ChatMemberService {

    private final ChatMemberRepository chatMemberRepository;
    private final UserRepository userRepository;

    @Override
    public ChatMember create(ChatRoom chatRoom, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        ChatMember chatMember = ChatMember.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        return chatMemberRepository.save(chatMember);
    }
}
