package codeit.sb06.otboo.feed.controller;

import codeit.sb06.otboo.feed.dto.FeedCreateRequest;
import codeit.sb06.otboo.feed.dto.FeedDto;
import codeit.sb06.otboo.feed.service.FeedService;
import codeit.sb06.otboo.security.CurrentUserId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {

  private final FeedService service;

  @PostMapping
  public ResponseEntity<FeedDto> createFeed(@RequestBody FeedCreateRequest request) {
    return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
  }

  @DeleteMapping("/{feedId}")
  public ResponseEntity<Void> deleteFeed(@PathVariable UUID feedId) {
    service.delete(feedId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{feedId}")
  public ResponseEntity<FeedDto> updateFeed(
      @PathVariable UUID feedId,
      @RequestBody String content,
      @CurrentUserId UUID currentUserId
  ) {
    return ResponseEntity.ok(service.update(feedId, currentUserId, content));
  }
}
