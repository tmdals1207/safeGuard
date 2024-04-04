package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.request.LoginRequestDTO;
import com.capstone.safeGuard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainService {
    private final MemberRepository memberRepository;

    public Boolean login(LoginRequestDTO dto) {
        // TODO 로그인 로직

        return false;
    }

    public Boolean signup(){
        // TODO 중복 회원 조회 및 회원가입 로직


        return false;
    }
}
