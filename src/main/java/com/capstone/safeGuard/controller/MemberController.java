package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Authority;
import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.TokenInfo;
import com.capstone.safeGuard.dto.request.ChildSignUpRequestDTO;
import com.capstone.safeGuard.dto.request.LoginRequestDTO;
import com.capstone.safeGuard.dto.request.SignUpRequestDTO;
import com.capstone.safeGuard.service.LoginType;
import com.capstone.safeGuard.service.MemberService;
import com.capstone.safeGuard.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("member") LoginRequestDTO dto,
                        BindingResult bindingResult,
                        HttpServletResponse response){
        if (bindingResult.hasErrors()){
            return "login";
        }

        // Member 타입으로 로그인 하는 경우
        if(dto.getLoginType().equals(LoginType.Member.toString())){
            Member memberLogin = memberService.memberLogin(dto);
            if(memberLogin.getName().isBlank())
                return "login";

            // member가 존재하는 경우 token을 전달
            TokenInfo tokenInfo = generateTokenOfMember(memberLogin);
            response.setHeader("Authorization", "Bearer"+tokenInfo.getAccessToken());
        }
        // Child 타입으로 로그인 하는 경우
        else{
            Child childLogin = memberService.childLogin(dto);
            if(childLogin.getChildName().isBlank())
                return "login";

            // child가 존재하는 경우 token을 전달
            TokenInfo tokenInfo = generateTokenOfChild(childLogin);
            response.setHeader("Authorization", "Bearer"+tokenInfo.getAccessToken());
            log.info("Login Success = {}", tokenInfo.getAccessToken());
        }

        return "/home";
    }

    @GetMapping("/signup")
    public String showMemberSignUpForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String memberSignUp(@Validated @ModelAttribute("member") SignUpRequestDTO dto,
                               BindingResult bindingResult){
        log.info("dto = {}", dto.getInputId());
        if(bindingResult.hasErrors()){
            log.info("bindingResult = {}", bindingResult);
            return "signup";
        }

        Boolean signUpSuccess = memberService.signup(dto);
        if(! signUpSuccess){
            log.info("signupFail = {}", signUpSuccess);
            return "signup";
        }
        log.info("signup success = {}", signUpSuccess);
        return "redirect:/login";   //로그인 페이지로 리다이렉트
    }

    @GetMapping("/childsignup")
    public String showChildSignUpForm() {
        return "group";
    }

    @PostMapping("/childsignup")
    public String childSignUp(@Validated @ModelAttribute("child") ChildSignUpRequestDTO dto,
                              BindingResult bindingResult){
        log.info("name = {}", dto.getChildName());

        if(bindingResult.hasErrors()){
            return "group";
        }
        Boolean signUpSuccess = memberService.childSignUp(dto);
        if(! signUpSuccess){
            return "signup";
        }
        log.info("child_name = {}", dto.getChildName());
        log.info("아이 회원가입 성공!");
        return "redirect:/group";   //그룹관리 페이지로 리다이렉트
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
