package com.capstone.safeGuard.dto.request;

import lombok.Data;

@Data
public class ChildSignUpRequestDTO {
    private String memberId;
    private long childId;
    private String childPassword;
    private String childName;

    public Long getChild_id() {
        return childId;
    }

    public CharSequence getChild_password() {
        return childPassword;
    }
}
