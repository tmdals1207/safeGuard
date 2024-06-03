package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.ChildFile;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.domain.MemberFile;
import com.capstone.safeGuard.repository.ChildFileRepository;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.MemberFileRepository;
import com.capstone.safeGuard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    public static final String SAVEPATH = "/Users/snhng/Documents/testing/";
    private final MemberFileRepository memberFileRepository;
    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;
    private final ChildFileRepository childFileRepository;

    @Transactional
    public String saveMemberFile(MultipartFile file, String memberId) {
        String originalFilename = file.getOriginalFilename();
        UUID uuid = UUID.randomUUID();
        String filePath = SAVEPATH + uuid + "-" + originalFilename;

        Optional<Member> foundMember = memberRepository.findById(memberId);
        if (foundMember.isEmpty()) {
            return null;
        }

        Optional<MemberFile> foundMemberFile = memberFileRepository.findByMember(foundMember.get());
        foundMemberFile.ifPresent(memberFileRepository::delete);

        try {
            file.transferTo(new File(filePath));
            memberFileRepository.save(
                    MemberFile.builder()
                            .fileName(filePath)
                            .member(foundMember.get())
                            .build()
            );

        } catch (IOException e) {
            return null;
        }
        return filePath;
    }

    @Transactional
    public String saveChildFile(MultipartFile file, String childName) {
        String originalFilename = file.getOriginalFilename();
        UUID uuid = UUID.randomUUID();
        String filePath = SAVEPATH + uuid + "-" + originalFilename;

        Child foundChild = childRepository.findByChildName(childName);
        if (foundChild == null) {
            return null;
        }

        Optional<ChildFile> foundChildFile = childFileRepository.findByChild(foundChild);
        foundChildFile.ifPresent(childFileRepository::delete);

        try {
            file.transferTo(new File(filePath));
            childFileRepository.save(
                    ChildFile.builder()
                            .fileName(filePath)
                            .child(foundChild)
                            .build()
            );

        } catch (IOException e) {
            return null;
        }
        return filePath;
    }

    @Transactional
    public String findMemberFile(String userId) {
        Optional<Member> foundMember = memberRepository.findById(userId);
        if (foundMember.isEmpty()) {
            return null;
        }

        Optional<MemberFile> foundFile = memberFileRepository.findByMember(foundMember.get());
        return foundFile.map(MemberFile::getFileName).orElse(null);
    }

    @Transactional
    public String findChildFile(String userId) {
        Child foundChild = childRepository.findByChildName(userId);
        if (foundChild == null) {
            return null;
        }

        Optional<ChildFile> foundFile = childFileRepository.findByChild(foundChild);
        return foundFile.map(ChildFile::getFileName).orElse(null);
    }
}
