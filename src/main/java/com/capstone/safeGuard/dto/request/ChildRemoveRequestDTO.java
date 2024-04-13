package com.capstone.safeGuard.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChildRemoveRequestDTO {
    private Long child_id;
    @NotBlank
    private String childName;
}
