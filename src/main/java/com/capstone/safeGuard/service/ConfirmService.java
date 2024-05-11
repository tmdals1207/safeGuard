package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.domain.Parenting;
import com.capstone.safeGuard.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfirmService {
    private final ChildRepository childRepository;

    public boolean sendConfirm(Member sender, Child child) {
        // 1. child의 parent 리스트 찾기
        List<Parenting> parentingList = child.getParentingList();
        if(parentingList.isEmpty()){
            return false;
        }

        // 2. child의 모든 parent에게 알림 전송
        for (Parenting parenting : parentingList) {
            Member parent = parenting.getParent();
            // TODO 알림 전송 부분 notice에서 사용한 코드 재사용 해서 개발 해주시면 됩니다.

        }

        return true;
    }
}
