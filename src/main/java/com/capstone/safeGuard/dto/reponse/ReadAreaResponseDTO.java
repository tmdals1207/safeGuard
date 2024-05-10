package com.capstone.safeGuard.dto.reponse;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadAreaResponseDTO {
    private String isLiving;
    private String firstX;
    private String firstY;
    private String secondX;
    private String secondY;
    private String thirdX;
    private String thirdY;
    private String fourthX;
    private String fourthY;

    @Builder
    public ReadAreaResponseDTO(String isLiving, String firstX, String firstY, String secondX, String secondY, String thirdX, String thirdY, String fourthX, String fourthY) {
        this.isLiving = isLiving;
        this.firstX = firstX;
        this.firstY = firstY;
        this.secondX = secondX;
        this.secondY = secondY;
        this.thirdX = thirdX;
        this.thirdY = thirdY;
        this.fourthX = fourthX;
        this.fourthY = fourthY;
    }
}
