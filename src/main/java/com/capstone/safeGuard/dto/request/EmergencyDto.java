package com.capstone.safeGuard.dto.request;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Emergency;
import com.capstone.safeGuard.domain.Member;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmergencyDto {
    private Long emergencyId;
    private String memberId;
    private String childName;
    private double latitude;
    private double longitude;


    public Emergency dtoToDomain(Member member, Child child){
        return new Emergency()
                .builder()
                .title("제목 뭘로 할지 생각")
                .content("내용 뭘로 할지 생각")
                .senderId(member)
                .child(child)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
