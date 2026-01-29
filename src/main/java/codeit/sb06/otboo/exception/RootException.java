package codeit.sb06.otboo.exception;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class RootException extends RuntimeException{

    private final Instant timestamp;
    private final Map<String, Object> details;

    public RootException(String message) {
        super(message);
        this.timestamp = Instant.now();
        this.details = new HashMap<>();
    }

    public RootException(String message, Throwable cause) {
        super(message, cause);
        this.timestamp = Instant.now();
        this.details = new HashMap<>();
    }

    public void addDetail(String key, Object value) {
        this.details.put(key, value);
    }

}
