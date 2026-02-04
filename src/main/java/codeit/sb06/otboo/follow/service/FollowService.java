package codeit.sb06.otboo.follow.service;

import codeit.sb06.otboo.follow.dto.FollowCreateRequest;
import codeit.sb06.otboo.follow.dto.FollowDto;

public interface FollowService {
  FollowDto createFollow(FollowCreateRequest followCreateRequest);
}
