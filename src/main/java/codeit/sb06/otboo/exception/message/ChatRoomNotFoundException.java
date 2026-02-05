package codeit.sb06.otboo.exception.message;

public class ChatRoomNotFoundException extends MessageException {

    private static final String DEFAULT_MESSAGE = "채팅방을 찾을 수 없습니다.";

    public ChatRoomNotFoundException() {
        super(DEFAULT_MESSAGE, 404);
    }
}
