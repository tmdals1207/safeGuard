package com.capstone.safeGuard.dto;

import com.capstone.safeGuard.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class MemberDto {
    private String type; // 부모(parent) 또는 조력자(helper)
    private String memberId;
    private String name;
    private String email;
    private String password;


    public static MemberDto memberDto (Member member) {
        MemberDto dto = new MemberDto();
        dto.memberId = member.getMemberId();
        dto.name = member.getName();
        dto.email = member.getEmail();
        dto.password = member.getPassword();
        return dto;
    }
}