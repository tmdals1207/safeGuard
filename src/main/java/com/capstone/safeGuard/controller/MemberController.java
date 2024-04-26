package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Authority;
import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.TokenInfo;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


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
    public ResponseEntity<Map<String, String>> login(@Validated @RequestBody LoginRequestDTO dto,
                                                     BindingResult bindingResult,
                                                     HttpServletResponse response,
                                                     HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();

        if (bindingResult.hasErrors()) {
            result.put("status", "403");
            return ResponseEntity.status(403).body(result);
        }

        // Member 타입으로 로그인 하는 경우
        if (dto.getLoginType().equals(LoginType.Member.toString())) {
            Member memberLogin = memberService.memberLogin(dto);
            if (memberLogin == null) {
                return addErrorStatus(result);
            }

            // member가 존재하는 경우 token을 전달
            TokenInfo tokenInfo = generateTokenOfMember(memberLogin);
            log.info(tokenInfo.getGrantType());
            log.info(tokenInfo.getAccessToken());
            log.info(tokenInfo.getRefreshToken());

            storeTokenInBody(response, result, tokenInfo);
            // 생성한 토큰을 저장
            jwtService.storeToken(tokenInfo);

            // 세션에 memberid 저장
            HttpSession session = request.getSession();
            session.setAttribute("memberid", memberLogin.getMemberId());

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
        }
        return ResponseEntity.ok().body(result);
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
    public ResponseEntity memberSignUp(@Validated @RequestBody SignUpRequestDTO dto,
                                       BindingResult bindingResult) {

        String errorMessage = memberService.validateBindingError(bindingResult);
        if (errorMessage != null) {
            return ResponseEntity.badRequest().body(errorMessage);
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

        String errorMessage = memberService.validateBindingError(bindingResult);
        if (errorMessage != null) {
            return ResponseEntity.badRequest().body(errorMessage);
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
    public ResponseEntity childRemove(@Validated @RequestBody Map<String, String> requestBody,
                                      HttpServletRequest request, BindingResult bindingResult) {

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
    public ResponseEntity addHelper(@Validated @RequestBody Map<String, String> requestBody,
                                    HttpServletRequest request, BindingResult bindingResult) {


        String errorMessage = memberService.validateBindingError(bindingResult);
        if (errorMessage != null) {
            return ResponseEntity.badRequest().body(errorMessage);
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

    @GetMapping("/member-logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        String requestToken = request.getHeader("Authorization");
        try {
            jwtService.findByToken(requestToken);
        } catch (Exception e) {
            return addErrorStatus(result);
        }
        boolean isLogoutSuccess = memberService.logout(requestToken);

        HttpSession session = request.getSession(false);

        if (isLogoutSuccess) {
            if (session != null) {
                session.removeAttribute("memberid"); // 세션에서 memberid 삭제
                session.invalidate(); // 세션 무효화
            }

            result.put("status", "200");
            return ResponseEntity.ok().body(result);
        }
        return addErrorStatus(result);
    }

    private static ResponseEntity<Map<String, String>> addErrorStatus(Map<String, String> result) {
        result.put("status", "400");
        return ResponseEntity.status(400).body(result);
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
