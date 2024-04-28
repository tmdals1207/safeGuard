package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.*;
import com.capstone.safeGuard.dto.request.*;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.EmailAuthCodeRepository;
import com.capstone.safeGuard.repository.MemberRepository;
import com.capstone.safeGuard.repository.ParentingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;
    private final ParentingRepository parentingRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;
    private final EmailAuthCodeRepository emailAuthCodeRepository;

    private static final int emailAuthCodeDuration =  1800; // 30 * 60 * 1000 == 30분

    public Member memberLogin(LoginRequestDTO dto) {
        Optional<Member> findMember = memberRepository.findById(dto.getEditTextID());
        // ID가 없는 경우
        return findMember
                .map(member -> findMemberWithAuthenticate(member, dto.getEditTextPW()))
                .orElse(null);

        // 비밀번호 일치하는지 찾는 부분
    }

    private Member findMemberWithAuthenticate(Member findMember, String rawPassword) {
        if (passwordEncoder.matches(rawPassword, findMember.getPassword())) {
            return findMember;
        }
        return null;
    }

    public Child childLogin(LoginRequestDTO dto) {
        Optional<Child> findChild = Optional.ofNullable(childRepository.findBychildName(dto.getEditTextID()));

        return findChild
                .map(child -> findChildWithAuthenticate(child, dto.getEditTextPW()))
                .orElse(null);

    }

    private Child findChildWithAuthenticate(Child findChild, String rawPassword) {
        if (passwordEncoder.matches(rawPassword, findChild.getChildPassword())) {
            return findChild;
        }
        return null;
    }

    public Boolean signup(SignUpRequestDTO dto) {
        Optional<Member> findMember = memberRepository.findById(dto.getInputID());
        if (findMember.isPresent()) {
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


    public Boolean childSignUp(ChildSignUpRequestDTO dto) {
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
        String memberId = dto.getMemberId();
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

    public Boolean childRemove(String childName) {
        Child selectedChild = childRepository.findBychildName(childName);
        if (selectedChild == null) {
            return false;
        }
        childRepository.delete(selectedChild);
        return true;
    }

    public boolean logout(String accessToken) {
        try {
            jwtService.toBlackList(accessToken);
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    public String validateBindingError(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : errors) {
                errorMessage.append(error.getDefaultMessage()).append("\n");
            }
            return errorMessage.toString();
        }
        return null;
    }

    public String findMemberId(FindMemberIdDTO dto) {
        Member foundMember = memberRepository.findByEmail(dto.getEmail());

        if (foundMember == null || (! foundMember.getName().equals(dto.getName()) ) ) {
            return null;
        }

        return foundMember.getMemberId();
    }

    public String findChildId(FindChildIdDTO dto) {
        Optional<Member> foundParent = memberRepository.findById(dto.getParentId());
        if (foundParent.isEmpty()) {
            return null;
        }

        List<Parenting> parentingList = foundParent.get().getParentingList();
        if(parentingList.isEmpty()){
            return null;
        }

        return childIDsBuilder(parentingList);
    }

    private String childIDsBuilder(List<Parenting> parentingList) {
        StringBuilder childIds = new StringBuilder();
        int index = 0;

        childIds.append("{");
        for (Parenting parenting : parentingList) {
            childIds.append("\"ChildID")
                    .append(index+1)
                    .append("\" : \"")
                    .append(parenting.getChild().getChildName())
                    .append("\"");

            if(index < parentingList.size() - 1){
                childIds.append(",");
            }
        }
        childIds.append("}");

        return childIds.toString();
    }

    public boolean sendCodeToEmail(String memberId) {
        Optional<Member> foundMember = memberRepository.findById(memberId);
        if (foundMember.isEmpty()){
            return false;
        }

        String address = foundMember.get().getEmail();
        String title = "SafeGuard 이메일 인증 번호";
        String authCode = createCode();

        mailService.sendEmail(address, title, authCode);
        Optional<EmailAuthCode> foundCode = emailAuthCodeRepository.findById(memberId);
        if(foundCode.isPresent()){
            emailAuthCodeRepository.delete(foundCode.get());
        }
        emailAuthCodeRepository.save(new EmailAuthCode(address, authCode, LocalDateTime.now()));
        return true;
    }

    private String createCode() {
        int length = 6;
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(random.nextInt(10));
        }

        return builder.toString();
    }


    public boolean verifiedCode(String memberId, String authCode) {
        Optional<Member> foundMember = memberRepository.findById(memberId);
        if (foundMember.isEmpty()){
            return false;
        }

        Optional<EmailAuthCode> foundCode = emailAuthCodeRepository.findById(foundMember.get().getEmail());
        if (foundCode.isEmpty()){
            return false;
        }

        if (Duration.between(foundCode.get().getCreatedAt(), LocalDateTime.now()).getSeconds()
                > emailAuthCodeDuration){
            return false;
        }

        return authCode.equals(foundCode.get().getAuthCode());
    }

    @Transactional
    public boolean resetMemberPassword(ResetPasswordDTO dto){
        Optional<Member> foundMember = memberRepository.findById(dto.getId());

        if(foundMember.isEmpty()){
            return false;
        }

        foundMember.get().setPassword(passwordEncoder.encode(dto.getNewPassword()));
        return true;
    }

    public ArrayList<String> findChildList(String memberId) {
        Optional<Member> foundMember = memberRepository.findById(memberId);

        if(foundMember.isEmpty()){
            throw new NoSuchElementException();
        }

        List<Parenting> parentingList = foundMember.get().getParentingList();
        ArrayList<String> childNameList = new ArrayList<>();

        for (Parenting parenting : parentingList) {
            childNameList.add(parenting.getChild().getChildName());
        }

        return childNameList;
    }

    public boolean resetChildPassword(ResetPasswordDTO dto) {
        Child foundChild = childRepository.findBychildName(dto.getId());

        if (foundChild == null){
            return false;
        }

        foundChild.setChildPassword(passwordEncoder.encode(dto.getNewPassword()));
        return true;
    }
}