package com.capstone.safeGuard.dto.request.confirm;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.ConfirmType;
import com.capstone.safeGuard.domain.Emergency;
import com.capstone.safeGuard.domain.Member;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.ErrorResponse;

import java.time.LocalDateTime;
@Data
@Getter
public class SendConfirmRequest{
    private String senderId;
    private String childName;
    private ConfirmType confirmType;
    private  String content;

}
