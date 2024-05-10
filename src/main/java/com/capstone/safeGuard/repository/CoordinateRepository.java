package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Coordinate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface CoordinateRepository extends JpaRepository<Coordinate, Long> {

    ArrayList<Coordinate> findAllByChild(Child child);
}
