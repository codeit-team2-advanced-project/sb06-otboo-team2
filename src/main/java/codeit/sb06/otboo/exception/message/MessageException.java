package codeit.sb06.otboo.exception.message;

import codeit.sb06.otboo.exception.RootException;

public class MessageException extends RootException {

    public MessageException(String message, int status) {
        super(message, status);
    }

    public MessageException(String message, Throwable cause, int status) {
        super(message, cause, status);
    }
}
