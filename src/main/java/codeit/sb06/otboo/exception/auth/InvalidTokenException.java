package codeit.sb06.otboo.exception.auth;

public class InvalidTokenException extends AuthException{

    private static final String DEFAULT_MESSAGE = "Invalid token";

    public InvalidTokenException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause, 401);
    }

    public InvalidTokenException() {
        super(DEFAULT_MESSAGE, 401);
    }
}
