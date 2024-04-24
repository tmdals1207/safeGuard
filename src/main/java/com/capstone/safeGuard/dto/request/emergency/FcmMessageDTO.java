package com.capstone.safeGuard.dto.request.emergency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class FcmMessageDTO {
    private boolean validateOnly;
    private FcmMessageDTO.Message messge;


    @Builder
    @Getter @AllArgsConstructor
    public class Message {
        private FcmMessageDTO.Notification notification;
        private String token;
    }

    @Builder
    @Getter @AllArgsConstructor
    public class Notification {
        private String title;
        private String body;
    }

}
