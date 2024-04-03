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
@Table(name="parent")
public class Parent {
    @Id
    private String parentId;
    @OneToOne
    @MapsId
    @JsonIgnore
    private Member member;

    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    private List<Parenting> parentingList;
}
