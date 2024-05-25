package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Confirm;
import com.capstone.safeGuard.domain.Helping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ConfirmRepository extends JpaRepository<Confirm, Long> {
    ArrayList<Confirm> findAllByHelpingId(Helping helping);

    ArrayList<Confirm> findAllByChild(Child child);
}
