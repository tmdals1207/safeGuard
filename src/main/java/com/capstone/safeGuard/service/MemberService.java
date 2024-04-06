package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.request.ChildSignUpRequestDTO;
import com.capstone.safeGuard.dto.request.LoginRequestDTO;
import com.capstone.safeGuard.dto.request.SignUpRequestDTO;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;

    public Boolean login(LoginRequestDTO dto) {
        if(dto.getLoginType().equals(LoginType.Member.toString())){
            return memberLogin(dto);
        }else{
            return childLogin(dto);
        }
    }

    private boolean memberLogin(LoginRequestDTO dto) {
        Optional<Member> findMember = memberRepository.findById(dto.getEditTextID());
        if(findMember.isEmpty()){
            return true;
        }

        return findMember.get()
                .getPassword()
                .equals(dto.getEditTextPW());
    }

    private Boolean childLogin(LoginRequestDTO dto) {
        Optional<Child> findChild = Optional.ofNullable(childRepository.findBychildName(dto.getEditTextID()));
        Child child = findChild.orElse(null);

        if (child != null && child.getChild_password().equals(dto.getEditTextPW())) {
            // 로그인 성공 처리
            return true;
        } else {
            // 로그인 실패 처리 (아이디 또는 비밀번호 불일치)
            return false;
        }
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
        member.setPassword(dto.getInputPW());
        memberRepository.save(member);

        return true;
    }

    public Boolean childSignUp(ChildSignUpRequestDTO dto){

        Child child = new Child();
        child.setChild_id(dto.getChild_id());
        child.setChildName(dto.getChildName());
        child.setChild_password(dto.getChild_password());
        childRepository.save(child);

        return true;
    }
}
