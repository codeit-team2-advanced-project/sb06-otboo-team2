package codeit.sb06.otboo.notification.dto;

import lombok.Builder;

@Builder
public record SseEvent(
        String id,
        String name,
        Object data
) {
    public static SseEvent of(String eventId, String eventName, Object data) {
        return SseEvent.builder()
                .id(eventId)
                .name(eventName)
                .data(data)
                .build();
    }
}
