package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Authority;
import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.TokenInfo;
import com.capstone.safeGuard.dto.request.findidandresetpw.*;
import com.capstone.safeGuard.dto.request.signupandlogin.*;
import com.capstone.safeGuard.dto.request.updatecoordinate.ReturnCoordinateDTO;
import com.capstone.safeGuard.dto.request.updatecoordinate.UpdateCoordinateDTO;
import com.capstone.safeGuard.service.JwtService;
import com.capstone.safeGuard.service.LoginType;
import com.capstone.safeGuard.service.MemberService;
import com.capstone.safeGuard.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;


@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;
    private final NoticeController noticeController;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Validated @RequestBody LoginRequestDTO dto,
                                                     BindingResult bindingResult,
                                                     HttpServletResponse response,
                                                     HttpServletRequest request) {
        log.info(dto.getEditTextID());
        log.info(dto.getLoginType());

        Map<String, String> result = new HashMap<>();

        if (bindingResult.hasErrors()) {
            return addBindingError(result);
        }

        // Member 타입으로 로그인 하는 경우
        if (dto.getLoginType().equals(LoginType.Member.toString())) {
            Member memberLogin = memberService.memberLogin(dto);
            if (memberLogin == null) {
                return addErrorStatus(result);
            }

            // member가 존재하는 경우 token을 전달
            TokenInfo tokenInfo = generateTokenOfMember(memberLogin);
            storeTokenInBody(response, result, tokenInfo);
            result.put("Type", "Member");
        }
        // Child 타입으로 로그인 하는 경우
        else {
            Child childLogin = memberService.childLogin(dto);
            if (childLogin == null) {
                return addErrorStatus(result);
            }

            // child가 존재하는 경우 token을 전달
            TokenInfo tokenInfo = generateTokenOfChild(childLogin);
            storeTokenInBody(response, result, tokenInfo);

            HttpSession session = request.getSession();
            session.setAttribute("childName", childLogin.getChildName());
            result.put("Type", "Child");
        }
        return addOkStatus(result);
    }

    private void storeTokenInBody(HttpServletResponse response, Map<String, String> result, TokenInfo tokenInfo) {
        response.setHeader("Authorization", tokenInfo.getAccessToken());
        // 생성한 토큰을 저장
        jwtService.storeToken(tokenInfo);
        result.put("authorization", tokenInfo.getAccessToken());
        result.put("status", "200");
    }

    @GetMapping("/signup")
    public String showMemberSignUpForm() {
        return "signup";
    }

    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> memberSignUp(@Validated @RequestBody SignUpRequestDTO dto,
                                       BindingResult bindingResult) {
        log.info(dto.getInputID());
        log.info(dto.getInputName());

        HashMap<String, String> result = new HashMap<>();

        String errorMessage = memberService.validateBindingError(bindingResult);
        if (errorMessage != null) {
            return addErrorStatus(result);
        }

        Boolean signUpSuccess = memberService.signup(dto);
        if (!signUpSuccess) {
            log.info("signupFail = {}", signUpSuccess);
            return addErrorStatus(result);
        }
        log.info("signup success = {}", signUpSuccess);
        return addOkStatus(result);
    }

    @GetMapping("/memberremove")
    public String showMemberRemoveForm() {
        return "login";
    }

    @PostMapping("/memberremove")
    public ResponseEntity memberRemove(@Validated @RequestBody Map<String, String> requestBody,
                                      BindingResult bindingResult) {

        String errorMessage = memberService.validateBindingError(bindingResult);
        if (errorMessage != null) {
            return ResponseEntity.badRequest().body(errorMessage);
        }

        String memberId = requestBody.get("memberId");

        Boolean RemoveSuccess = memberService.memberRemove(memberId);
        if (!RemoveSuccess) {
            return ResponseEntity.status(400).build();
        }

        log.info("멤버 삭제 성공!");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/childsignup")
    public String showChildSignUpForm() {
        return "group";
    }

    @PostMapping(value = "/childsignup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity childSignUp(@Validated @RequestBody ChildSignUpRequestDTO childDto,
                                      BindingResult bindingResult) {
        log.info("childSignup 실행");

        String errorMessage = memberService.validateBindingError(bindingResult);
        if (errorMessage != null) {
            return ResponseEntity.badRequest().body(errorMessage);
        }

        Boolean signUpSuccess = memberService.childSignUp(childDto);
        if (!signUpSuccess) {
            log.info("signupFail = {}", signUpSuccess);
            return ResponseEntity.status(400).build();
        }
        log.info("signup success = {}", signUpSuccess);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/childremove")
    public String showChildRemoveForm() {
        return "group";
    }

    @PostMapping("/childremove")
    public ResponseEntity childRemove(@Validated @RequestBody Map<String, String> requestBody,
                                       BindingResult bindingResult) {

        String errorMessage = memberService.validateBindingError(bindingResult);
        if (errorMessage != null) {
            return ResponseEntity.badRequest().body(errorMessage);
        }

        String childName = requestBody.get("childName");

        Boolean RemoveSuccess = memberService.childRemove(childName);
        if (!RemoveSuccess) {
            return ResponseEntity.status(400).build();
        }

        log.info("아이 삭제 성공!");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addhelper")
    public ResponseEntity addHelper(@Validated @RequestBody AddMemberDto addMemberDto,
                                    BindingResult bindingResult) {


        String errorMessage = memberService.validateBindingError(bindingResult);
        if (errorMessage != null) {
            return ResponseEntity.badRequest().body(errorMessage);
        }

        Boolean addSuccess = memberService.addHelper(addMemberDto);

        if (!addSuccess) {
            log.info("add Fail = {}", addSuccess);
            return ResponseEntity.status(400).build();
        }
        log.info("add success = {}", addSuccess);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/helperremove")
    public ResponseEntity helperRemove(@Validated @RequestBody Map<String, String> requestBody,
                                      BindingResult bindingResult) {

        String errorMessage = memberService.validateBindingError(bindingResult);
        if (errorMessage != null) {
            return ResponseEntity.badRequest().body(errorMessage);
        }

        String helperName = requestBody.get("helperName");

        Boolean RemoveSuccess = memberService.helperRemove(helperName);
        if (!RemoveSuccess) {
            return ResponseEntity.status(400).build();
        }

        log.info("헬퍼 삭제 성공!");
        return ResponseEntity.ok().build();
    }

    //로그인한 멤버의 자식(그룹)들을 찾아서 반환
    @PostMapping("/group")
    public List<Child> showChildList (@Validated @RequestBody Map<String, String> requestBody) {

        String memberId = requestBody.get("memberId");
        log.info(memberId + "의 자식 리스트 반환 ");
        List<Child> childList = memberService.getChildList(memberId);
        if(childList == null) {
            log.info("NULL");
        }

        return childList;
    }

    @GetMapping("/member-logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        String requestToken = request.getHeader("Authorization");
        try {
            jwtService.findByToken(requestToken);
        } catch (Exception e) {
            return addErrorStatus(result);
        }
        if (memberService.logout(requestToken)) {
            return addOkStatus(result);
        }
        return addErrorStatus(result);
    }

    @PostMapping("/find-member-id")
    public ResponseEntity<Map<String, String>> findMemberId(@Validated @RequestBody FindMemberIdDTO dto,
                                       BindingResult bindingResult) {
        Map<String, String> result = new HashMap<>();

        if (bindingResult.hasErrors()) {
            return addBindingError(result);
        }

        String memberId = memberService.findMemberId(dto);
        if (memberId == null) {
            return addErrorStatus(result);
        }

        result.put("status", "200");
        result.put("memberId", memberId);

        return ResponseEntity.ok().body(result);
    }

    // 비밀번호 확인을 위한 이메일 인증 1
    // 인증번호 전송
    @PostMapping("/verification-email-request")
    public ResponseEntity<Map<String, String>> verificationEmailRequest(@RequestBody EmailRequestDTO dto) {
        Map<String, String> result = new HashMap<>();
        if(! memberService.sendCodeToEmail(dto.getInputId())){
            // 해당 아이디가 존재하지 않음
            return addErrorStatus(result);
        }
        return addOkStatus(result);
    }

    // 비밀번호 확인을 위한 이메일 인증 2
    // 인증번호 확인
    @PostMapping("/verification-email")
    public ResponseEntity<Map<String, String>> verificationEmail(@RequestBody VerificationEmailDTO dto) {
        Map<String, String> result = new HashMap<>();
        if (! memberService.verifiedCode(dto.getInputId(), dto.getInputCode())){
            // 코드가 틀렸다는 메시지와 함께 다시 입력하는 곳으로 리다이렉트
            return addErrorStatus(result);
        }
        // 비밀번호 재설정 팝업 or 리다이렉트

        return addOkStatus(result);
    }

    // 비밀번호 확인을 위한 이메일 인증 3
    @PostMapping("/reset-member-password")
    public ResponseEntity<Map<String, String>> resetMemberPassword(@RequestBody ResetPasswordDTO dto) {
        Map<String, String> result = new HashMap<>();

        if(! memberService.resetMemberPassword(dto)) {
            return addErrorStatus(result);
        }
        return addOkStatus(result);
    }

    @PostMapping("/find-child-list")
    public ResponseEntity<Map<String, String>> findChildNameList(@Validated @RequestBody GetMemberIdDTO dto) {
        return getChildList(dto.getMemberId());
    }

    @PostMapping("/chose-child-form")
    public ResponseEntity<Map<String, String>> choseChildForm(@RequestBody GetMemberIdDTO dto){
        return getChildList(dto.getMemberId());
    }

    @PostMapping("/chose-child")
    public ResponseEntity<Map<String, String>> choseChildToChangePassword(@RequestBody ResetPasswordDTO dto){
        Map<String, String> result = new HashMap<>();

        if(! memberService.resetChildPassword(dto)){
            return addErrorStatus(result);
        }

        result.put("status", "200");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-coordinate")
    public ResponseEntity<Map<String, String>> updateCoordinate(@RequestBody UpdateCoordinateDTO dto){
        Map<String, String> result = new HashMap<>();

        if(dto.getType().equals("Member")){
            if (memberService.updateMemberCoordinate(dto.getId(), dto.getLatitude(), dto.getLongitude())){
                return addOkStatus(result);
            }
            return addErrorStatus(result);
        }
        if(memberService.updateChildCoordinate(dto.getId(), dto.getLatitude(), dto.getLongitude())){
            noticeController.sendNotice(dto.getId());
            return addOkStatus(result);
        }
        return addErrorStatus(result);
    }

    @PostMapping("/return-coordinate")
    public ResponseEntity<Map<String, Double>> returnCoordinate(@RequestBody ReturnCoordinateDTO dto) {
        Map<String, Double> coordinates;
        log.info("위치 전송 시작");

        if (dto.getType().equals("Member")) {
            coordinates = memberService.getMemberCoordinate(dto.getId());
            if (coordinates != null) {
                return ResponseEntity.ok(coordinates);
            }
        } else if (dto.getType().equals("Child")) {
            coordinates = memberService.getChildCoordinate(dto.getId());
            if (coordinates != null) {
                noticeController.sendNotice(dto.getId());
                return ResponseEntity.ok(coordinates);
            }
        } else {
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @PostMapping("/duplicate-check-member")
    public ResponseEntity<Map<String, String>> duplicateCheckMember(@RequestBody GetIdDTO dto){
        Map<String, String> result = new HashMap<>();
        if(memberService.isPresent(dto.getId(), true)){
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }

    @PostMapping("/duplicate-check-child")
    public ResponseEntity<Map<String, String>> duplicateCheckChild(@RequestBody GetIdDTO dto){
        Map<String, String> result = new HashMap<>();
        if(memberService.isPresent(dto.getId(), false)){
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }

    private ResponseEntity<Map<String, String>> getChildList(String memberId) {
        Map<String, String> result = new HashMap<>();

        ArrayList<String> childList;
        try {
            childList = memberService.findChildList(memberId);
        } catch (NoSuchElementException e){
            return addErrorStatus(result);
        }

        result.put("status", "200");
        for (int i = 0; i < childList.size(); i++) {
            result.put(String.valueOf(i+1), childList.get(i));
        }

        return ResponseEntity.ok().body(result);
    }

    private static ResponseEntity<Map<String, String>> addOkStatus(Map<String, String> result) {
        result.put("status", "200");
        return ResponseEntity.ok().body(result);
    }

    private static ResponseEntity<Map<String, String>> addErrorStatus(Map<String, String> result) {
        result.put("status", "400");
        return ResponseEntity.status(400).body(result);
    }

    private ResponseEntity<Map<String, Object>> addOkStatusWithData(Map<String, Object> result, Map<String, Double> coordinates) {
        result.put("status", "ok");
        result.put("data", coordinates);
        return ResponseEntity.ok(result);
    }

    private ResponseEntity<Map<String, Object>> addErrorStatusWithData(Map<String, Object> result) {
        result.put("status", "error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    private static ResponseEntity<Map<String, String>> addBindingError(Map<String, String> result) {
        result.put("status", "403");
        return ResponseEntity.status(403).body(result);
    }

    public TokenInfo generateTokenOfMember(Member member) {
        Authentication authentication
                = new UsernamePasswordAuthenticationToken(member.getMemberId(), member.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(Authority.ROLE_MEMBER.toString())));
        return jwtTokenProvider.generateToken(authentication);
    }


    public TokenInfo generateTokenOfChild(Child child) {
        Authentication authentication
                = new UsernamePasswordAuthenticationToken(child.getChildName(), child.getChildPassword(),
                Collections.singleton(new SimpleGrantedAuthority(Authority.ROLE_CHILD.toString())));
        return jwtTokenProvider.generateToken(authentication);
    }
}
