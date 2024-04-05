package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.request.LoginRequestDTO;
import com.capstone.safeGuard.dto.request.SignUpRequestDTO;
import com.capstone.safeGuard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member memberLogin(LoginRequestDTO dto) {
        Optional<Member> findMember = memberRepository.findById(dto.getEditTextID());
        if(findMember.isEmpty()){
            return null;
        }

        return findMemberWithAuthenticate(findMember, dto.getEditTextPW());
    }

    private Member findMemberWithAuthenticate(Optional<Member> findMember, String rawPassword){
        return findMember
                .filter(member -> passwordEncoder.matches(rawPassword, member.getPassword()))
                .orElseThrow(IllegalArgumentException::new);
    }

    public Child childLogin(LoginRequestDTO dto) {
        return null;
    }

    public Boolean signup(SignUpRequestDTO dto){
        Optional<Member> findMember = memberRepository.findById(dto.getInputId());
        if(findMember.isPresent()){
            return false;
        }

        Member member = new Member();
        member.setMemberId(dto.getInputId());
        member.setEmail(dto.getInputEmail());
        member.setName(dto.getInputName());
        String encodedPassword = passwordEncoder.encode(dto.getInputPW());
        member.setPassword(encodedPassword);
        memberRepository.save(member);

        return true;
    }
}
