package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.request.ChildSignUpRequestDTO;
import com.capstone.safeGuard.dto.request.LoginRequestDTO;
import com.capstone.safeGuard.dto.request.SignUpRequestDTO;
import com.capstone.safeGuard.service.LoginType;
import com.capstone.safeGuard.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }


    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("member") LoginRequestDTO dto,
                        BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "login";
        }

        if(dto.getLoginType().equals(LoginType.Member.toString())){
            Member memberLogin = memberService.memberLogin(dto);
            if(memberLogin.getName().isBlank())
                return "login";
        }else{
            Child childLogin = memberService.childLogin(dto);
            if(childLogin.getChildName().isBlank())
                return "login";
        }

        return "redirect:/";    //메인페이지로 리다이렉트
    }

    @GetMapping("/signup")
    public String showSignUpForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(@Validated @ModelAttribute("member") SignUpRequestDTO dto,
                         BindingResult bindingResult){
        log.info("dto = {}", dto.getInputId());
        if(bindingResult.hasErrors()){
            return "signup";
        }

        Boolean signUpSuccess = memberService.signup(dto);
        if(! signUpSuccess){
            return "signup";
        }

        return "redirect:/login";   //로그인 페이지로 리다이렉트
    }
    @PostMapping("/childsignup")
    public String childsignUp(@Validated @ModelAttribute("child") ChildSignUpRequestDTO dto,
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
}
