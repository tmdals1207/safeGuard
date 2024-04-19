package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Authority;
import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.TokenInfo;
import com.capstone.safeGuard.dto.request.ChildRemoveRequestDTO;
import com.capstone.safeGuard.dto.request.ChildSignUpRequestDTO;
import com.capstone.safeGuard.dto.request.LoginRequestDTO;
import com.capstone.safeGuard.dto.request.SignUpRequestDTO;
import com.capstone.safeGuard.service.JwtService;
import com.capstone.safeGuard.service.LoginType;
import com.capstone.safeGuard.service.MemberService;
import com.capstone.safeGuard.util.JwtAuthenticationFilter;
import com.capstone.safeGuard.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtService jwtService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity login(@Validated @RequestBody LoginRequestDTO dto,
                                BindingResult bindingResult,
                                HttpServletResponse response,
                                HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(404).build();
        }

        // Member 타입으로 로그인 하는 경우
        if (dto.getLoginType().equals(LoginType.Member.toString())) {
            Member memberLogin = memberService.memberLogin(dto);
            if (memberLogin.getName().isBlank())
                return ResponseEntity.status(404).build();

            // member가 존재하는 경우 token을 전달
            TokenInfo tokenInfo = generateTokenOfMember(memberLogin);
            response.setHeader("Authorization", "Bearer" + tokenInfo.getAccessToken());

            // 생성한 토큰을 저장
            jwtService.storeToken(tokenInfo);

            // 세션에 memberid 저장
            HttpSession session = request.getSession();
            session.setAttribute("memberid", memberLogin.getMemberId());

        }
        // Child 타입으로 로그인 하는 경우
        else {
            Child childLogin = memberService.childLogin(dto);
            if (childLogin.getChildName().isBlank())
                return ResponseEntity.status(404).build();

            // child가 존재하는 경우 token을 전달
            TokenInfo tokenInfo = generateTokenOfChild(childLogin);
            response.setHeader("Authorization", "Bearer" + tokenInfo.getAccessToken());

            // 생성한 토큰을 저장
            jwtService.storeToken(tokenInfo);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/signup")
    public String showMemberSignUpForm() {
        return "signup";
    }

    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity memberSignUp(@Validated @RequestBody SignUpRequestDTO dto,
                                       BindingResult bindingResult) {
        log.info("dto = {}", dto.getInputID());
        if (bindingResult.hasErrors()) {
            log.info("bindingResult = {}", bindingResult);
            log.info("실패 binding error ");
            return ResponseEntity.status(400).build();
        }

        Boolean signUpSuccess = memberService.signup(dto);
        if (!signUpSuccess) {
            log.info("signupFail = {}", signUpSuccess);
            return ResponseEntity.status(400).build();
        }
        log.info("signup success = {}", signUpSuccess);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/childsignup")
    public String showChildSignUpForm() {
        return "group";
    }

    @PostMapping(value = "/childsignup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity childSignUp(@Validated @RequestBody ChildSignUpRequestDTO dto,
                                      BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : errors) {
                errorMessage.append(error.getDefaultMessage()).append("\n");
            }
            return ResponseEntity.badRequest().body(null);
        }

        //로그인 하고 있는 member의 id를 가져옴
        HttpSession session = request.getSession(false); // 새로운 세션을 생성하지 않음
        Boolean signUpSuccess;
        if (session != null) {
            String memberId = (String) session.getAttribute("memberid");
            if (memberId != null) {
                signUpSuccess = memberService.childSignUp(dto, memberId);
            } else {
                return ResponseEntity.status(400).build();
            }
        } else {
            return ResponseEntity.status(400).build();
        }


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
    public String childRemove(@Validated @ModelAttribute("child") ChildRemoveRequestDTO dto,
                              BindingResult bindingResult) {
        log.info("name = {}", dto.getChildName());

        if (bindingResult.hasErrors()) {
            return "group";
        }
        Boolean RemoveSuccess = memberService.childRemove(dto);
        if (!RemoveSuccess) {
            return "group";
        }
        log.info("child_name = {}", dto.getChildName());
        log.info("아이 삭제 성공!");
        return "redirect:/group";   //그룹관리 페이지로 리다이렉트
    }

    @PostMapping("/addhelper")
    public ResponseEntity addHelper(@Validated @RequestBody Map<String, String> requestBody,
                                    HttpServletRequest request, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : errors) {
                errorMessage.append(error.getDefaultMessage()).append("\n");
            }
            return ResponseEntity.badRequest().body(null);
        }

        String childName = requestBody.get("childName");

        HttpSession session = request.getSession();
        session.setAttribute("selectedChildName", childName);


        session = request.getSession(false); // 새로운 세션을 생성하지 않음
        String memberId = (String) session.getAttribute("memberid");
        Boolean addSuccess;
        if (session != null) {
            if (childName != null) {
                addSuccess = memberService.addHelper(memberId, childName);
            } else {
                return ResponseEntity.status(400).build();
            }
        } else {
            return ResponseEntity.status(400).build();
        }

        if (!addSuccess) {
            log.info("add Fail = {}", addSuccess);
            return ResponseEntity.status(400).build();
        }
        log.info("add success = {}", addSuccess);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        String accessToken = jwtAuthenticationFilter.resolveToken(request);
        boolean isLogout = memberService.logout(accessToken);

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("memberid"); // 세션에서 memberid 삭제
            session.invalidate(); // 세션 무효화
        }

        if (isLogout) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(401).build();
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
