package codeit.sb06.otboo.feed.controller;

import codeit.sb06.otboo.exception.feed.FeedException;
import codeit.sb06.otboo.feed.dto.FeedCreateRequest;
import codeit.sb06.otboo.feed.dto.FeedDto;
import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {

  private final FeedService service;

  @PostMapping
  public ResponseEntity<FeedDto> createFeed(@RequestBody FeedCreateRequest request) {
    return ResponseEntity.status(201).body(service.create(request));
  }
}
