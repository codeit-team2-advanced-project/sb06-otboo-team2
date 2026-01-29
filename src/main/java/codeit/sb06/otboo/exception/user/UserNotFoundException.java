package codeit.sb06.otboo.exception.user;

public class UserNotFoundException extends UserException{

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
