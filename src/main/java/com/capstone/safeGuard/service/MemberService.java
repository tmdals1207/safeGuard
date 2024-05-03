package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.*;
import com.capstone.safeGuard.dto.request.findidandresetpw.*;
import com.capstone.safeGuard.dto.request.signupandlogin.*;
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
        member.setFcmToken(dto.getFcmToken());
        memberRepository.save(member);

        return true;
    }


    public Boolean childSignUp(ChildSignUpRequestDTO childDto, ParentingDto parentingDto) {
        Optional<Child> findChild = Optional.ofNullable(childRepository.findBychildName(childDto.getChildName()));
        if (findChild.isPresent()) {
            return false;
        }
        log.info(childDto.getMemberId());
        log.info(childDto.getChildName());
        log.info(childDto.getChildPassword());
        log.info(String.valueOf(childDto.getChildId()));

        Child child = new Child();
        child.setChildName(childDto.getChildName());
        String encodedPassword = passwordEncoder.encode(childDto.getChild_password());
        child.setChildPassword(encodedPassword);
        child.setAuthority(Authority.ROLE_CHILD);
        childRepository.save(child);

        // member child 연결
        String memberId = childDto.getMemberId();
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isEmpty()) {
            return false;
        }
        saveParenting(parentingDto, memberId, child);

        return true;
    }

    public void saveParenting(ParentingDto parentingDto, String memberId, Child child) {
        // 부모와 자식 엔티티의 ID를 사용하여 엔티티 객체를 가져옴
        Optional<Member> parent = memberRepository.findById(memberId);

        if (parent.isEmpty() || child==null) {
            // 부모나 자식이 존재하지 않는 경우 처리
            return;
        }

        log.info("child : " + child.getChildName());
        log.info("parent : " + memberRepository.findById(memberId));

        // Parenting 엔티티 생성
        Parenting parenting = new Parenting();
        parenting.setParent(parent.get());
        parenting.setChild(child);

        // Parenting 엔티티 저장
        parentingRepository.save(parenting);
    }

    //TODO helping 추가 재구현 필요
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

    public String findChildNamesByParentId(String parentId) {
        Optional<Member> foundParent = memberRepository.findById(parentId);
        if (foundParent.isEmpty()) {
            return null;
        }

        List<Parenting> parentingList = foundParent.get().getParentingList();
        if(parentingList.isEmpty()){
            return null;
        }

        return childNamesBuilder(parentingList);
    }

    private String childNamesBuilder(List<Parenting> parentingList) {
        StringBuilder childIds = new StringBuilder();
        int index = 0;

        childIds.append("{");
        for (Parenting parenting : parentingList) {
            childIds.append("\"ChildName")
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

    public ArrayList<Member> findAllMember() {
        return new ArrayList<>(memberRepository.findAll());
    }

    @Transactional
    public boolean updateMemberCoordinate(String id, float latitude, float longitude) {
        Optional<Member> foundMember = memberRepository.findById(id);
        if(foundMember.isEmpty()){
            return false;
        }

        foundMember.get().setLatitude(latitude);
        foundMember.get().setLongitude(longitude);

        return true;
    }

    // 해당 메소드에서 id는 child의 name이다.
    @Transactional
    public boolean updateChildCoordinate(String id, float latitude, float longitude) {
        Child foundChild = childRepository.findBychildName(id);
        if(foundChild == null){
            return false;
        }

        foundChild.setLatitude(latitude);
        foundChild.setLongitude(longitude);

        return true;
    }
}