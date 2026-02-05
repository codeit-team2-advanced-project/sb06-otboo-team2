package codeit.sb06.otboo.exception.profile;

import codeit.sb06.otboo.exception.RootException;

public class ProfileException extends RootException {

    public ProfileException(String message, int status) {
        super(message, status);
    }

    public ProfileException(String message, Throwable cause, int status) {
        super(message, cause, status);
    }
}
