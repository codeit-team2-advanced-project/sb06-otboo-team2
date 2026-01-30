package codeit.sb06.otboo.exception.clothes;

import codeit.sb06.otboo.exception.RootException;

public class ClothesException extends RootException {

    public ClothesException(String message, int status) {
        super(message, status);
    }

    public ClothesException(String message, Throwable cause, int status) {
        super(message, cause, status);
    }
}
