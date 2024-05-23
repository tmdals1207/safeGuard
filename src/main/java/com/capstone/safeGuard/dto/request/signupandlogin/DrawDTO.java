package com.capstone.safeGuard.dto.request.signupandlogin;

import lombok.Getter;

@Getter
public class DrawDTO {
    private String memberID;

    public DrawDTO() {}

    public DrawDTO(String memberID) {
        this.memberID = memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }
}