package codeit.sb06.otboo.exception.notification;

import codeit.sb06.otboo.exception.RootException;

public class NotificationException extends RootException {
    public NotificationException(String message, int status) {
        super(message, status);
    }

    public NotificationException(String message, Throwable cause, int status) {
        super(message, cause, status);
    }
}
