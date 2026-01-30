package codeit.sb06.otboo.exception.user;

public class UserNotFoundException extends UserException{

    private static final String DEFAULT_MESSAGE = "User not found";

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE, 404);
    }

    public UserNotFoundException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause, 404);
    }
}
