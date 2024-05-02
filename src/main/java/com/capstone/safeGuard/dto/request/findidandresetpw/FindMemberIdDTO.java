package com.capstone.safeGuard.dto.request.findidandresetpw;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class FindMemberIdDTO {
    private String name;
    @Email
    private String email;
}
