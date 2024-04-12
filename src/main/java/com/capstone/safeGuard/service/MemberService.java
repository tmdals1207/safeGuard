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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;
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
        Optional<Child> findChild = Optional.ofNullable(childRepository.findBychildName (dto.getEditTextID()));
        Child child = findChild.orElse(null);

        if(findChild.isEmpty()){
            return null;
        }

        return findChildWithAuthenticate(findChild, dto.getEditTextPW());
    }
    private Child findChildWithAuthenticate(Optional<Child> findChild, String rawPassword){
        return findChild
                .filter(child -> passwordEncoder.matches(rawPassword, child.getChildPassword()))
                .orElseThrow(IllegalArgumentException::new);
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

    public void logout(String token){

    }

}