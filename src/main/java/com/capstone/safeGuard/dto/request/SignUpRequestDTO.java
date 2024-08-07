package com.capstone.safeGuard.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignUpRequestDTO {
    @NotBlank
    private String inputId;
    @NotBlank
    private String inputName;
    @NotBlank
    @Email
    private String inputEmail;
    @NotBlank
    private String inputPW;
}
