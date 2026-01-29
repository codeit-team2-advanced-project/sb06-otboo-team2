package codeit.sb06.otboo.exception.auth;

public class InvalidTokenException extends AuthException{

    private final String message = "Refresh token is invalid";

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
