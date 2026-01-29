package codeit.sb06.otboo.exception.user;

import codeit.sb06.otboo.exception.RootException;

public class UserException extends RootException {

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}
