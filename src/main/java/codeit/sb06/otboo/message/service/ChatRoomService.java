package codeit.sb06.otboo.message.service;

import codeit.sb06.otboo.message.entity.ChatRoom;

import java.util.UUID;

public interface ChatRoomService {

    ChatRoom getOrCreatePrivateRoom(UUID senderId, UUID receiverId);
}
