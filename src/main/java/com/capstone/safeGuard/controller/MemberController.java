package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.dto.request.LoginRequestDTO;
import com.capstone.safeGuard.dto.request.SignUpRequestDTO;
import com.capstone.safeGuard.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
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

        Boolean loginSuccess = memberService.login(dto);
        if(! loginSuccess){
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
        if(bindingResult.hasErrors()){
            return "signup";
        }

        Boolean signUpSuccess = memberService.signup(dto);
        if(! signUpSuccess){
            return "signup";
        }

        return "redirect:/login";   //로그인 페이지로 리다이렉트
    }
}
