package com.capstone.safeGuard.dto.request.signupandlogin;

import lombok.Data;

@Data
public class ChildSignUpRequestDTO {
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
