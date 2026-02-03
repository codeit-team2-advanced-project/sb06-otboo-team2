package codeit.sb06.otboo.exception.profile;

public class ProfileNotFoundException extends ProfileException{

    private static final String DEFAULT_MESSAGE = "Profile not found";

    public ProfileNotFoundException() {
        super(DEFAULT_MESSAGE, 404);
    }

    public ProfileNotFoundException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause, 404);
    }
}
