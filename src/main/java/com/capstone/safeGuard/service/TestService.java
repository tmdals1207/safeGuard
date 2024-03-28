package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.TestDomain;
import com.capstone.safeGuard.dto.TestDTO;
import com.capstone.safeGuard.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;

    public boolean dbConnectionTest(TestDTO testDTO) {
        if (!validateDuplicateMember(testDTO)) {
            TestDomain domain = TestDomain.toDomain(testDTO);
            testRepository.save(domain);
            return true; // 회원 저장 성공
        }
        return false; // 중복 회원
    }

    public boolean validateDuplicateMember(TestDTO testDTO) {
        Optional<TestDomain> existingMember = testRepository.findByName(testDTO.getName());
        return existingMember.isPresent(); // 이미 존재하는 회원이면 true 반환
    }

    public TestDTO dbConnectionTestFind(Integer i) {
        Optional<TestDomain> domain = testRepository.findById(i);
        return TestDTO.toDTO(domain.get());
    }
}
