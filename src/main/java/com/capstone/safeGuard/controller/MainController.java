package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.dto.request.LoginRequestDTO;
import com.capstone.safeGuard.dto.request.SignUpRequestDTO;
import com.capstone.safeGuard.service.MainService;
import com.capstone.safeGuard.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final TestService testService;
    private final MainService mainService;

    public String home(){
        return "home";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute LoginRequestDTO dto,
                        BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "signup";
        }

        Boolean loginSuccess = mainService.login(dto);
        if(! loginSuccess){
            return "signup";
        }

        return "redirect:/";    //메인페이지로 리다이렉트
    }

    @GetMapping("/signup")
    public String showSignUpForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(@Validated @ModelAttribute SignUpRequestDTO dto,
                         BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "signup";
        }

        Boolean signUpSuccess = mainService.signup();
        if(! signUpSuccess){
            return "signup";
        }

        return "redirect:/login";   //로그인 페이지로 리다이렉트

    }
}
