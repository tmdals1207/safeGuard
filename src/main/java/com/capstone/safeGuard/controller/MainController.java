package com.capstone.safeGuard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MainController {

    public String home(){
        return "home";
    }


}
