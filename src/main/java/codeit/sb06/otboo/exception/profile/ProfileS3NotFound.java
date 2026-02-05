package codeit.sb06.otboo.exception.profile;

public class ProfileS3NotFound extends ProfileException{

    private static final String DEFAULT_MESSAGE = "프로필 이미지가 S3에 존재하지 않습니다.";

    public ProfileS3NotFound() {
        super(DEFAULT_MESSAGE, 400);
    }

    public ProfileS3NotFound(Throwable cause) {
        super(DEFAULT_MESSAGE, cause, 400);
    }
}
