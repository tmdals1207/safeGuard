package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.dto.TestDTO;
import com.capstone.safeGuard.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @GetMapping("/db-connection-test-save")
    public String dbConnectionTestSave(){
        TestDTO dto = new TestDTO("홍길동", 20);
        testService.dbConnectionTest(dto);

        return "testing";
    }

    @GetMapping("/db-connection-test-find")
    public String dbConnectionTestFind(@RequestParam Integer id){
        TestDTO dto = testService.dbConnectionTestFind(id);
        String name = dto.getName();

        return name;
    }

    @PostMapping("/save-data")
    public String saveData(@RequestParam String name, @RequestParam int age) {
        TestDTO dto = new TestDTO(name, age);
        testService.dbConnectionTest(dto);

        return name + " 추가 완료";
    }

}
