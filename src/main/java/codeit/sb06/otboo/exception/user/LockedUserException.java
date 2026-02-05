package codeit.sb06.otboo.exception.user;

public class LockedUserException extends UserException {

    private static final String DEFAULT_MESSAGE = "잠긴 계정입니다.";

    public LockedUserException() {
        super(DEFAULT_MESSAGE, 401);
    }

    public LockedUserException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause, 401);
    }
}
