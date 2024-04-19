package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Authority;
import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.request.ChildSignUpRequestDTO;
import com.capstone.safeGuard.dto.request.LoginRequestDTO;
import com.capstone.safeGuard.dto.request.SignUpRequestDTO;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public Member memberLogin(LoginRequestDTO dto) {
        Optional<Member> findMember = memberRepository.findById(dto.getEditTextID());
        // ID가 없는 경우
        return findMember
                .map(member -> findMemberWithAuthenticate(member, dto.getEditTextPW()))
                .orElse(null);

        // 비밀번호 일치하는지 찾는 부분
    }

    private Member findMemberWithAuthenticate(Member findMember, String rawPassword){
        if(passwordEncoder.matches(rawPassword, findMember.getPassword())){
            return findMember;
        }
        return null;
    }

    public Child childLogin(LoginRequestDTO dto) {
        Optional<Child> findChild = Optional.ofNullable(childRepository.findBychildName (dto.getEditTextID()));

        return findChild
                .map(child -> findChildWithAuthenticate(child, dto.getEditTextPW()))
                .orElse(null);

    }
    private Child findChildWithAuthenticate(Child findChild, String rawPassword){
        if(passwordEncoder.matches(rawPassword, findChild.getChildPassword())){
            return findChild;
        }
        return null;
    }

    public Boolean signup(SignUpRequestDTO dto){
        Optional<Member> findMember = memberRepository.findById(dto.getInputID());
        if(findMember.isPresent()){
            return false;
        }

        Member member = new Member();
        member.setMemberId(dto.getInputID());
        member.setEmail(dto.getInputEmail());
        member.setName(dto.getInputName());
        String encodedPassword = passwordEncoder.encode(dto.getInputPW());
        member.setPassword(encodedPassword);
        member.setAuthority(Authority.ROLE_MEMBER);
        memberRepository.save(member);

        return true;
    }


    public Boolean childSignUp(ChildSignUpRequestDTO dto){
        Optional<Child> findChild = Optional.ofNullable(childRepository.findBychildName(dto.getChildName()));
        if(findChild.isPresent()){
            return false;
        }

        Child child = new Child();
        child.setChildId(dto.getChild_id());
        child.setChildName(dto.getChildName());
        String encodedPassword = passwordEncoder.encode(dto.getChild_password());
        child.setChildPassword(encodedPassword);
        child.setAuthority(Authority.ROLE_CHILD);
        childRepository.save(child);

        return true;
    }

    public boolean logout(String accessToken){
        try {
            jwtService.toBlackList(accessToken);
        }catch (NoSuchElementException e){
            return false;
        }
        return true;
    }

}