package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    public Child findByChildName(String name); // child_name 사용

    @Query(value = "SELECT parent.parent_id from child childRow " +
            "LEFT JOIN parenting parent ON parent.child_id = childRow.child_id " +
            "WHERE childRow.child_name = :childName ", nativeQuery = true)
    public List<String> findAllMemberByChildName(@Param("childName") String childName);


    void delete(Child child);

    Child findBychildName(String childName);

}
