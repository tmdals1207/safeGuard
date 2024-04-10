package com.capstone.safeGuard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/home")
    public String showHome() {
        return "home";
    }
    @GetMapping("/group")
    public String showGroup() {
        return "group";
    }

    @GetMapping("/option")
    public String showOption() {
        return "option";
    }
}
