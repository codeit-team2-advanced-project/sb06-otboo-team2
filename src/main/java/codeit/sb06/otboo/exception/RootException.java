package codeit.sb06.otboo.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class RootException extends RuntimeException{

    private final LocalDateTime timestamp;
    private final int status;
    private final Map<String, Object> details;

    public RootException(String message, int status) {
        super(message);
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.details = new HashMap<>();
    }

    public RootException(String message, Throwable cause, int status) {
        super(message, cause);
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.details = new HashMap<>();
    }

    public void addDetail(String key, Object value) {
        this.details.put(key, value);
    }

}
