package codeit.sb06.otboo.exception.clothes;

public class ClothesImageUploadFailedException extends ClothesException {
    private static final String DEFAULT_MESSAGE = "이미지 업로드에 실패했습니다.";

    public ClothesImageUploadFailedException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause, 500);
    }
}
