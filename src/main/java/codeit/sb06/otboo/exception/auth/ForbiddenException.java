package codeit.sb06.otboo.exception.auth;

public class ForbiddenException extends AuthException {

    private static final String DEFAULT_MESSAGE = "Access is denied";

    public ForbiddenException() {
        super(DEFAULT_MESSAGE, 403);
    }

    public ForbiddenException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause, 403);
    }
}
