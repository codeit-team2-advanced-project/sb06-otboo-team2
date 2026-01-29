package codeit.sb06.otboo.exception.auth;

import codeit.sb06.otboo.exception.RootException;

public class AuthException extends RootException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
