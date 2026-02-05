package codeit.sb06.otboo.follow.service;

import codeit.sb06.otboo.follow.dto.FollowCreateRequest;
import codeit.sb06.otboo.follow.dto.FollowDto;
import codeit.sb06.otboo.follow.dto.FollowSummaryDto;
import java.util.UUID;

public interface FollowService {
  FollowDto createFollow(FollowCreateRequest followCreateRequest);
  FollowSummaryDto getFollowSummary(UUID userId);
}
