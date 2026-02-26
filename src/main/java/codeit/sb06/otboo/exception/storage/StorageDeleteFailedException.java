package codeit.sb06.otboo.exception.storage;

import org.springframework.http.HttpStatus;

public class StorageDeleteFailedException extends StorageException {

  private static final String DEFAULT_MESSAGE = "파일 삭제에 실패했습니다.";

  public StorageDeleteFailedException(String key, Throwable cause) {
    super(DEFAULT_MESSAGE, cause, HttpStatus.INTERNAL_SERVER_ERROR.value());
    addDetail("key", key);
  }
}
