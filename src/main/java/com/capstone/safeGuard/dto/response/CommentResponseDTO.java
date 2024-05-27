package com.capstone.safeGuard.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentResponseDTO {
    private Long commentId;
    private String content;
    private String commentator;
    private String commentDate;

    @Builder

    public CommentResponseDTO(Long commentId, String content, String commentator, String commentDate) {
        this.commentId = commentId;
        this.content = content;
        this.commentator = commentator;
        this.commentDate = commentDate;
    }
}
