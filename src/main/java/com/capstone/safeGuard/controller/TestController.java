package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.dto.TestDTO;
import com.capstone.safeGuard.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @GetMapping("/db-connection-test-save")
    public String dbConnectionTestSave() {
        TestDTO dto = new TestDTO("홍길동", 20, "부모");
        testService.dbConnectionTest(dto);

        return "testing";
    }

    @GetMapping("/db-connection-test-find")
    public String dbConnectionTestFind(@RequestParam Integer id) {
        TestDTO dto = testService.dbConnectionTestFind(id);
        String name = dto.getName();

        return name;
    }

    @PostMapping("/save-data")
    public String saveData(@RequestParam String name, @RequestParam int age, @RequestParam String relationship, Model model) {
        TestDTO dto = new TestDTO(name, age, relationship);
        if (testService.dbConnectionTest(dto)) {
            model.addAttribute("message", "회원가입이 완료되었습니다.");
            return "redirect:login"; // 회원가입이 성공하면 로그인 페이지로 리다이렉트
        } else {
            model.addAttribute("message", "이미 아이디가 있습니다.");
            return "redirect:signup"; // 중복 회원일 경우 회원가입 페이지로 리다이렉트
        }
    }
}
