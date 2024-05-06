package com.capstone.safeGuard.dto.request.emergency;

import lombok.Getter;

@Getter
public class CommentRequestDTO {
    private String commentatorId;
    private String commentContent;
    private String emergencyId;
}
