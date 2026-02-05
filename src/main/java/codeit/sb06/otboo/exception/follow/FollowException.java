package codeit.sb06.otboo.exception.follow;

import codeit.sb06.otboo.exception.RootException;

public class FollowException extends RootException {

  public FollowException(String message,int status) {
    super(message,status);
  }
  public FollowException(String message,Throwable cause,int status) {
    super(message, cause, status);
  }

}
