package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Helper;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.domain.Parent;
import com.capstone.safeGuard.dto.MemberDto;
import com.capstone.safeGuard.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public void signUp(MemberDto memberDto) {
        Member member;
        if ("parent".equals(memberDto.getType())) {
            member = new Parent();
        } else if ("helper".equals(memberDto.getType())) {
            member = new Helper();
        } else {
            throw new IllegalArgumentException("객체 생성 오류");
        }

        member.setName(memberDto.getName());
        member.setPassword(memberDto.getPassword());
        member.setEmail(memberDto.getEmail());

        member = memberRepository.save(member);
        ResponseEntity.ok(member);
    }


    public ResponseEntity<Member> logIn(MemberDto memberDto) {
        Member member = memberRepository.findBymemberIdAndPassword(memberDto.getMemberId(), memberDto.getPassword());

        if (member == null) {
            return ResponseEntity.notFound().build();  // 로그인 실패 시 Not Found (404)
        }

        return ResponseEntity.ok(member);  // 로그인 성공 시 Member 객체 반환 (200 OK)
    }
}
