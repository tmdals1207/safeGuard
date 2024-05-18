package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Confirm;
import com.capstone.safeGuard.domain.ConfirmType;
import com.capstone.safeGuard.domain.Helping;
import com.capstone.safeGuard.repository.ConfirmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmService {
    private final ConfirmRepository confirmRepository;

    @Transactional
    public void saveConfirm(Child child, Helping helping, String confirmType) {
        Confirm confirm = new Confirm();
        if( confirmType.equals("ARRIVED") ){
            confirm.setConfirmType(ConfirmType.ARRIVED);
        } else if( confirmType.equals("DEPART") ){
            confirm.setConfirmType(ConfirmType.DEPART);
        } else {
            confirm.setConfirmType(ConfirmType.UNCONFIRMED);
        }
        confirm.setChild(child);
        // TODO 제목, 내용 적기
        confirm.setTitle("");
        confirm.setContent("");
        confirm.setCreatedAt(LocalDateTime.now());
        confirm.setHelping_id(helping);
        confirmRepository.save(confirm);
    }
}
