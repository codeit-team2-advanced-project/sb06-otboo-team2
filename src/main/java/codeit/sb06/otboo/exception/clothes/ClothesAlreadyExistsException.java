package codeit.sb06.otboo.exception.clothes;

public class ClothesAlreadyExistsException extends ClothesException {
    private static final String DEFAULT_MESSAGE = "이미 존재하는 리소스입니다.";

    public ClothesAlreadyExistsException(String target, String value) {
        super(DEFAULT_MESSAGE + " target=" + target + ", value=" + value, 400);
    }

    public ClothesAlreadyExistsException(String target, String value, Throwable cause) {
        super(DEFAULT_MESSAGE + " target=" + target + ", value=" + value, cause, 400);
    }
}
