package codeit.sb06.otboo.exception.auth;

public class InvalidUserDetailException extends AuthException{

    public InvalidUserDetailException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUserDetailException(String message) {
        super(message);
    }
}
