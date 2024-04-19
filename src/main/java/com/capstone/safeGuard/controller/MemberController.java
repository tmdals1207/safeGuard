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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.HashMap;
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
    public ResponseEntity<Map<String, String>> login(@Validated @RequestBody LoginRequestDTO dto,
                                                     BindingResult bindingResult,
                                                     HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();

        if (bindingResult.hasErrors()) {
            result.put("status", "403");
            return ResponseEntity.status(403).body(result);
        }

        // Member 타입으로 로그인 하는 경우
        if (dto.getLoginType().equals(LoginType.Member.toString())) {
            Member memberLogin = memberService.memberLogin(dto);
            if (memberLogin == null) {
                result.put("status", "400");
                return ResponseEntity.status(400).body(result);
            }

            // member가 존재하는 경우 token을 전달
            TokenInfo tokenInfo = generateTokenOfMember(memberLogin);
            log.info(tokenInfo.getGrantType());
            log.info(tokenInfo.getAccessToken());
            log.info(tokenInfo.getRefreshToken());

            storeTokenInBody(response, result, tokenInfo);
        }
        // Child 타입으로 로그인 하는 경우
        else {
            Child childLogin = memberService.childLogin(dto);
            if (childLogin == null){
                result.put("status", "400");
                return ResponseEntity.status(400).body(result);
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

    @PostMapping("/childsignup")
    public String childSignUp(@Validated @ModelAttribute("child") ChildSignUpRequestDTO dto,
                              BindingResult bindingResult) {
        log.info("name = {}", dto.getChildName());

        if (bindingResult.hasErrors()) {
            return "group";
        }
        Boolean signUpSuccess = memberService.childSignUp(dto);
        if (!signUpSuccess) {
            return "signup";
        }
        log.info("child_name = {}", dto.getChildName());
        log.info("아이 회원가입 성공!");
        return "redirect:/group";   //그룹관리 페이지로 리다이렉트
    }

    @GetMapping("/member-logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        String requestToken = request.getHeader("Authorization");
        try {
            jwtService.findByToken(requestToken);
        }catch (Exception e){
            result.put("status", "400");
            return ResponseEntity.status(401).body(result);
        }
        boolean isLogoutSuccess = memberService.logout(requestToken);

        if (isLogoutSuccess) {
            result.put("status", "200");
            return ResponseEntity.ok().body(result);
        }
        result.put("status", "400");
        return ResponseEntity.status(401).body(result);
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
