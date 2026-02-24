package codeit.sb06.otboo.exception.message;

public class DirectMessageMappingException extends MessageException {

    private static final String DEFAULT_MESSAGE = "DM 매핑 중 오류가 발생했습니다.";

    public DirectMessageMappingException() {
        super(DEFAULT_MESSAGE, 500);
    }
}
