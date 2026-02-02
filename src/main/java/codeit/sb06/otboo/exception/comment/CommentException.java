package codeit.sb06.otboo.exception.comment;

import codeit.sb06.otboo.exception.RootException;

public class CommentException extends RootException {

  public CommentException(String message, int status) {
    super(message,status);
  }

  public CommentException(String message,Throwable cause, int status) {
    super(message,cause,status);
  }
}
