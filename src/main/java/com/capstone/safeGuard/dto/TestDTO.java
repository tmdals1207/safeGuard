package com.capstone.safeGuard.dto;

import com.capstone.safeGuard.domain.TestDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @AllArgsConstructor @RequiredArgsConstructor
public class TestDTO {
    private String name;
    private int age;

    public static TestDTO toDTO(TestDomain domain){
        TestDTO dto = new TestDTO();
        dto.name = domain.getName();
        dto.age = domain.getAge();

        return dto;
    }

    public int getAge() {
        return this.age;
    }

    public String getName() {
        return this.name;
    }
}
