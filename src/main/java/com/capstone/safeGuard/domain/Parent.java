package com.capstone.safeGuard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Table(name="parent")
public class Parent extends Member {
    @Id
    private String parentId;

    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    private List<Parenting> parentingList;
}
