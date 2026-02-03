package codeit.sb06.otboo.notification.dto;

import lombok.Builder;

@Builder
public record SseEvent(
        String eventId,
        String eventName,
        Object data
) {
    public static SseEvent of(String id, String eventName, Object data) {
        return SseEvent.builder()
                .eventId(id)
                .eventName(eventName)
                .data(data)
                .build();
    }
}
