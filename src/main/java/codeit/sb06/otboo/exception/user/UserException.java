package codeit.sb06.otboo.exception.user;

import codeit.sb06.otboo.exception.RootException;

public class UserException extends RootException {

    public UserException(String message, int status) {
        super(message, status);
    }

    public UserException(String message, Throwable cause, int status) {
        super(message, cause, status);
    }
}
