package codeit.sb06.otboo.message.dto;

public record DirectMessageCreatedRedisEvent(
        DirectMessageDto message,
        String destination
) {
}
