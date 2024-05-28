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
    private String child;

    @Builder
    public FindNotificationResponse(String title, String content, String date, String child) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.child = child;
    }
}
