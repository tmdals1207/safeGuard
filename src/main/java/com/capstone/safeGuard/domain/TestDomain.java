package com.capstone.safeGuard.domain;

import com.capstone.safeGuard.dto.TestDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity @Getter @Setter @RequiredArgsConstructor @Table(name = "test")
public class TestDomain {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column
    private String name;

    @Column
    private int age;

    public static TestDomain toDomain(TestDTO dto){
        TestDomain domain = new TestDomain();
        domain.name = dto.getName();
        domain.age = dto.getAge();

        return domain;
    }

}
