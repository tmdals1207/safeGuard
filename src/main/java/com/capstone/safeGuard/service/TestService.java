package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.TestDomain;
import com.capstone.safeGuard.dto.TestDTO;
import com.capstone.safeGuard.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;

    public void dbConnectionTest(TestDTO testDTO) {
        TestDomain domain = TestDomain.toDomain(testDTO);
        testRepository.save(domain);
    }

    public TestDTO dbConnectionTestFind(Integer i) {
        Optional<TestDomain> domain = testRepository.findById(i);

        return TestDTO.toDTO(domain.get());
    }
}
