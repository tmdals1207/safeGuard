package com.capstone.safeGuard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class EmergencyResponseDTO {
    private String emergencyTitle;
    private String emergencyContent;
    private String emergencyDate;
    private List<CommentResponseDTO> emergencyCommentList;

    @Builder

    public EmergencyResponseDTO(String emergencyTitle, String emergencyContent, String emergencyDate, List<CommentResponseDTO> emergencyCommentList) {
        this.emergencyTitle = emergencyTitle;
        this.emergencyContent = emergencyContent;
        this.emergencyDate = emergencyDate;
        this.emergencyCommentList = emergencyCommentList;
    }
}
