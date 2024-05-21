package com.capstone.safeGuard.dto.request.notification;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FCMNotificationDTO {
    public String receiverId;
    public String title;
    public String body;

    @Builder
    public FCMNotificationDTO(String receiverId, String title, String body) {
        this.receiverId = receiverId;
        this.title = title;
        this.body = body;
    }
}
