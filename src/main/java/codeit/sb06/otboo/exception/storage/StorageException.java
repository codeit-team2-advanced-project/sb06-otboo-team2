package codeit.sb06.otboo.exception.storage;

import codeit.sb06.otboo.exception.RootException;

public class StorageException extends RootException {

  public StorageException(String message, int status) {
    super(message, status);
  }

  public StorageException(String message, Throwable cause, int status) {
    super(message, cause, status);
  }
}
