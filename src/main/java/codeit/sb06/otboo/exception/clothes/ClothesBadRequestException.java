package codeit.sb06.otboo.exception.clothes;

public class ClothesBadRequestException extends ClothesException {
    private static final String DEFAULT_MESSAGE = "요청 값이 올바르지 않습니다.";

    public ClothesBadRequestException(String reason) {
        super(DEFAULT_MESSAGE + " reason=" + reason, 400);
    }

    public ClothesBadRequestException(String reason, Throwable cause) {
        super(DEFAULT_MESSAGE + " reason=" + reason, cause, 400);
    }
}
