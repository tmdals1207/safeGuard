package com.capstone.safeGuard.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChildSignUpRequestDTO {
    private Long child_id;
    @NotBlank
    private String childName;
    @NotBlank
    private String child_password;
}
