package codeit.sb06.otboo.exception.follow;

public class FollowNotFoundException extends FollowException {

  private static final String DEFAULT_MESSAGE = "팔로우가 존재하지 않습니다.";
  public FollowNotFoundException() {
    super(DEFAULT_MESSAGE,400);
  }
  public FollowNotFoundException(Throwable cause) {
    super(DEFAULT_MESSAGE,cause,400);
  }
}
