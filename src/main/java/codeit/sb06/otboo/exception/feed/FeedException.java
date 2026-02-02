package codeit.sb06.otboo.exception.feed;

import codeit.sb06.otboo.exception.RootException;
import java.util.List;
import java.util.UUID;

public class FeedException extends RootException {

    public FeedException(String message, int status) {
        super(message, status);
    }

    public FeedException(String message, Throwable cause, int status) {
        super(message, cause, status);
    }

    public static FeedException userNotFound(UUID userId) {
        FeedException ex = new FeedException("USER_NOT_FOUND", 404);
        ex.addDetail("userId", userId);
        return ex;
    }

    public static FeedException weatherNotFound(UUID weatherId) {
        FeedException ex = new FeedException("WEATHER_NOT_FOUND", 404);
        ex.addDetail("weatherId", weatherId);
        return ex;
    }

    public static FeedException clothesNotFound(List<UUID> clothesIds) {
        FeedException ex = new FeedException("CLOTHES_NOT_FOUND", 404);
        ex.addDetail("clothesIds", clothesIds);
        return ex;
    }

    public static FeedException invalidRequest(String message) {
        return new FeedException(message, 400);
    }
}
