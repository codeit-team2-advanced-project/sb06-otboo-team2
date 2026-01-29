package codeit.sb06.otboo.notification.publisher;

import codeit.sb06.otboo.notification.event.*;
import codeit.sb06.otboo.user.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishDirectMessageCreatedEvent(UUID targetId, String senderName, String content) {

        DirectMessageCreatedEvent event = DirectMessageCreatedEvent.builder()
                .targetId(targetId)
                .senderName(senderName)
                .content(content)
                .build();

        eventPublisher.publishEvent(event);
    }

    public void publishRoleUpdatedEvent(UUID targetId, Role role) {

        RoleUpdatedEvent event = RoleUpdatedEvent.builder()
                .targetId(targetId)
                .role(role)
                .build();

        eventPublisher.publishEvent(event);
    }

    public void publishClothesAttributeAddedEvent(UUID targetId, String newAttributeName) {

        ClothesAttributeAddedEvent event = ClothesAttributeAddedEvent.builder()
                .targetId(targetId)
                .attributeName(newAttributeName)
                .build();

        eventPublisher.publishEvent(event);
    }

    public void publishFeedLikedEvent(UUID targetId, String feedTitle, String likerName) {

        FeedLikedEvent event = FeedLikedEvent.builder()
                .targetId(targetId)
                .feedTitle(feedTitle)
                .likerName(likerName)
                .build();

        eventPublisher.publishEvent(event);
    }

    public void publishFeedCommentedEvent(UUID targetId, String feedTitle, String content) {

        FeedCommentedEvent event = FeedCommentedEvent.builder()
                .targetId(targetId)
                .feedTitle(feedTitle)
                .content(content)
                .build();

        eventPublisher.publishEvent(event);
    }

    public void publishFolloweeFeedPostedEvent(UUID targetId, String followeeName, String feedTitle) {

        FolloweeFeedPostedEvent event = FolloweeFeedPostedEvent.builder()
                .targetId(targetId)
                .followeeName(followeeName)
                .feedTitle(feedTitle)
                .build();

        eventPublisher.publishEvent(event);
    }

    public void publishFollowedEvent(UUID targetId, String followerName) {

        FollowedEvent event = FollowedEvent.builder()
                .targetId(targetId)
                .followerName(followerName)
                .build();

        eventPublisher.publishEvent(event);
    }
}
