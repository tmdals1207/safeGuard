package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.EmergencyReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyReceiverRepository extends JpaRepository<EmergencyReceiver, Long> {
    List<EmergencyReceiver> findAllByReceiverId(String memberId);
}
