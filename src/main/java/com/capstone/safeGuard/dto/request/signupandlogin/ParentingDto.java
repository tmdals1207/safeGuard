package com.capstone.safeGuard.dto.request.signupandlogin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor

public class ParentingDto {
    private String parentId;
    private String childName;
}
