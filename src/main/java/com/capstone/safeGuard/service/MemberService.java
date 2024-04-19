package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.*;
import com.capstone.safeGuard.dto.request.ChildRemoveRequestDTO;
import com.capstone.safeGuard.dto.request.ChildSignUpRequestDTO;
import com.capstone.safeGuard.dto.request.LoginRequestDTO;
import com.capstone.safeGuard.dto.request.SignUpRequestDTO;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.MemberRepository;
import com.capstone.safeGuard.repository.ParentingRepository;
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
    private final ParentingRepository parentingRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


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


    public Boolean childSignUp(ChildSignUpRequestDTO dto, String memberId) {
        Optional<Child> findChild = Optional.ofNullable(childRepository.findBychildName(dto.getChildName()));
        if (findChild.isPresent()) {
            return false;
        }

        Child child = new Child();
        child.setChildId(dto.getChild_id());
        child.setChildName(dto.getChildName());
        String encodedPassword = passwordEncoder.encode(dto.getChild_password());
        child.setChildPassword(encodedPassword);
        child.setAuthority(Authority.ROLE_CHILD);
        childRepository.save(child);

        // member child 연결
        Parenting parenting = new Parenting();
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isEmpty()) {
            return false;
        }
        parenting.setParent(findMember.get());
        parenting.setChild(child);
        parentingRepository.save(parenting);

        return true;
    }

    public Boolean addHelper(String memberId, String childName) {
        Helping helping = new Helping();
        Child selectedChild = childRepository.findBychildName(childName);
        if (selectedChild == null) {
            return false;
        }
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isEmpty()) {
            return false;
        }
        helping.setHelper(findMember.get());
        helping.setChild(selectedChild);

        return true;
    }

    public Boolean childRemove(ChildRemoveRequestDTO dto){
        Optional<Child> findChild = Optional.ofNullable(childRepository.findBychildName(dto.getChildName()));
        if(findChild.isEmpty()){
            return false;
        }
        childRepository.delete(findChild.get());
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