package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.MemberDto;
import com.capstone.safeGuard.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class MemberController {

    @Autowired
    private MemberService memberService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberDto memberDto) {

        Member member = memberService.logIn(memberDto).getBody();

        if (member == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }



    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestParam String type, @RequestParam String memberId,@RequestParam String name,@RequestParam String email,@RequestParam String password) {
        MemberDto memberDto = new MemberDto(type, memberId, name, email, password);

        memberService.signUp(memberDto);

        return ResponseEntity.ok().build();
    }
}
