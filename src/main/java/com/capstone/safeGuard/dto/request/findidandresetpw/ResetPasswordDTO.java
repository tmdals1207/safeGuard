package com.capstone.safeGuard.dto.request.findidandresetpw;

import lombok.Getter;

@Getter
public class ResetPasswordDTO {
    private String id;  //member -> ID, child -> Name
    private String newPassword;
}
