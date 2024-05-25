package com.capstone.safeGuard.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindNotificationResponse {
    private String title;
    private String content;
    private String date;
    private String type;
    private String child;

    @Builder
    public FindNotificationResponse(String title, String content, String date, String type, String child) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.type = type;
        this.child = child;
    }
}
