package codeit.sb06.otboo.exception.auth;

public class InvalidUserDetailException extends AuthException{

    private static final String DEFAULT_MESSAGE = "Invalid user details provided";

    public InvalidUserDetailException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause, 401);
    }

    public InvalidUserDetailException() {
        super(DEFAULT_MESSAGE, 401);
    }
}
