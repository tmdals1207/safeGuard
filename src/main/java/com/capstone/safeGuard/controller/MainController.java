package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final JwtService jwtService;

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


    @GetMapping("/check-auth")
    public String checkAuth(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Authorization");
        log.info("token: {}", token);
        if(jwtService.findByToken(token)){
            response.setStatus(401);
            return "group";
        }
        return "group";
    }
}
