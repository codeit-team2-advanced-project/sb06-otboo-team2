package codeit.sb06.otboo.exception.profile;

import codeit.sb06.otboo.profile.entity.Profile;

public class S3UploadFailedException extends ProfileException {

    private static final String DEFAULT_MESSAGE = "Failed to upload profile image to S3";

    public S3UploadFailedException() {
        super(DEFAULT_MESSAGE, 400);
    }

    public S3UploadFailedException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause, 400);
    }

}
