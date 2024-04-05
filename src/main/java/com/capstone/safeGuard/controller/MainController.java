package com.capstone.safeGuard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/signUp")
    public String signUp() {
        return "signUp";
    }

    @GetMapping("logIn")
    public String login() {
        return "login";
    }
}
