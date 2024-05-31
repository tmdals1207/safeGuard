package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.ChildBattery;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.domain.MemberBattery;
import com.capstone.safeGuard.repository.ChildBatteryRepository;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.MemberBatteryRepository;
import com.capstone.safeGuard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BatteryService {
    private final ChildBatteryRepository childBatteryRepository;
    private final MemberBatteryRepository memberBatteryRepository;
    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;

    @Transactional
    public void initChildBattery(String id, int battery) {
        Child foundChild = childRepository.findByChildName(id);

        childBatteryRepository.save(
                ChildBattery.builder()
                        .childName(foundChild)
                        .batteryValue(battery)
                        .build()
        );
    }

    @Transactional
    public boolean setChildBattery(String id, int battery) {
        Child foundChild = childRepository.findByChildName(id);

        Optional<ChildBattery> foundBattery = childBatteryRepository.findByChildName(foundChild);
        if (foundBattery.isEmpty()) {
            initChildBattery(id, battery);
            return true;
        }

        foundBattery.get().setBatteryValue(battery);
        return true;
    }

    @Transactional
    public ChildBattery getChildBattery(String id) {
        Child foundChild = childRepository.findByChildName(id);

        Optional<ChildBattery> foundBattery = childBatteryRepository.findByChildName(foundChild);
        return foundBattery.orElse(null);
    }


    @Transactional
    public void initMemberBattery(Member member, int battery) {
        memberBatteryRepository.save(
                MemberBattery.builder()
                        .memberId(member)
                        .batteryValue(battery).
                        build()
        );
    }

    @Transactional
    public boolean setMemberBattery(String id, int battery) {
        Optional<Member> foundMember = memberRepository.findById(id);
        if (foundMember.isEmpty()) {
            return false;
        }

        Optional<MemberBattery> foundBattery = memberBatteryRepository.findByMemberId(foundMember.get());
        if (foundBattery.isEmpty()) {
            initMemberBattery(foundMember.get(), battery);
            return true;
        }

        foundBattery.get().setBatteryValue(battery);
        return true;
    }

    @Transactional
    public MemberBattery getMemberBattery(String id) {
        Optional<Member> foundMember = memberRepository.findById(id);
        if (foundMember.isEmpty()) {
            return null;
        }

        Optional<MemberBattery> foundBattery = memberBatteryRepository.findByMemberId(foundMember.get());
        return foundBattery.orElse(null);
    }
}
