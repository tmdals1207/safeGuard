package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.ChildBattery;
import com.capstone.safeGuard.repository.ChildBatteryRepository;
import com.capstone.safeGuard.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BatteryService {
    private final ChildBatteryRepository childBatteryRepository;
    private final ChildRepository childRepository;

    @Transactional
    public void initBattery(String id, int battery) {
        Child foundChild = childRepository.findByChildName(id);

        childBatteryRepository.save(
                ChildBattery.builder()
                        .childName(foundChild)
                        .batteryValue(battery)
                        .build()
        );
    }

    @Transactional
    public boolean setBattery(String id, int battery) {
        Child foundChild = childRepository.findByChildName(id);

        Optional<ChildBattery> foundBattery = childBatteryRepository.findByChildName(foundChild);
        if (foundBattery.isEmpty()) {
            initBattery(id, battery);
            return true;
        }

        foundBattery.get().setBatteryValue(battery);
        return true;
    }

    @Transactional
    public ChildBattery getBattery(String id) {
        Child foundChild = childRepository.findByChildName(id);

        Optional<ChildBattery> foundBattery = childBatteryRepository.findByChildName(foundChild);
        return foundBattery.orElse(null);
    }
}
