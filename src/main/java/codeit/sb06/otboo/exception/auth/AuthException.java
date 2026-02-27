package codeit.sb06.otboo.exception.auth;

import codeit.sb06.otboo.exception.RootException;

public class AuthException extends RootException {

    public AuthException(String message, int status) {
        super(message, status);
    }

    public AuthException(String message, Throwable cause, int status) {
        super(message, cause, status);
    }
}
