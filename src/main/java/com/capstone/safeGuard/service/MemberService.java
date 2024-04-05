package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.request.LoginRequestDTO;
import com.capstone.safeGuard.dto.request.SignUpRequestDTO;
import com.capstone.safeGuard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Boolean login(LoginRequestDTO dto) {
        if(dto.getLoginType().equals(LoginType.Member)){
            return memberLogin(dto);
        }else{
            return childLogin(dto);
        }
    }

    private boolean memberLogin(LoginRequestDTO dto) {
        Optional<Member> findMember = memberRepository.findById(dto.getLoginId());
        if(findMember.isEmpty()){
            return true;
        }

        return findMember.get()
                .getPassword()
                .equals(dto.getPassword());
    }

    private Boolean childLogin(LoginRequestDTO dto) {
        return null;
    }

    public Boolean signup(SignUpRequestDTO dto){
        Optional<Member> findMember = memberRepository.findById(dto.getSignupId());
        if(findMember.isPresent()){
            return false;
        }

        Member member = new Member();
        member.setMemberId(dto.getSignupId());
        member.setEmail(dto.getEmail());
        member.setName(dto.getName());
        member.setPassword(dto.getPassword());
        memberRepository.save(member);

        return true;
    }
}
