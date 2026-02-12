package codeit.sb06.otboo.exception.feed;

import codeit.sb06.otboo.exception.RootException;
public class FeedException extends RootException {

    public FeedException(String message, int status) {
        super(message, status);
    }

    public FeedException(String message, Throwable cause, int status) {
        super(message, cause, status);
    }
}
