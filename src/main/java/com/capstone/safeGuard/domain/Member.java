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
@Table(name="member")
public class Member {
    @Id
    private Long memberId;
    private String name;
    private String password;
    private String email;

    @OneToOne(mappedBy = "member")
    @JsonIgnore
    private Parent parent;

    @OneToOne(mappedBy = "member")
    @JsonIgnore
    private Helper helper;

    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    private List<Parenting> parentingList;

    @OneToMany(mappedBy = "helper")
    @JsonIgnore
    private  List<Helping> helpingList;
}
