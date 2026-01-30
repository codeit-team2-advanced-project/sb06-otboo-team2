package codeit.sb06.otboo.exception.user;

public class UserAlreadyExistException extends UserException{

    private static final String DEFAULT_MESSAGE = "User already exists";

    public UserAlreadyExistException() {
        super(DEFAULT_MESSAGE, 400);
    }

    public UserAlreadyExistException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause, 400);
    }
}
