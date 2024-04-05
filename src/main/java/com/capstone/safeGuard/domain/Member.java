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
    private String memberId;
    private String name;
    private String password;
    private String email;

    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    private List<Parenting> parentingList;

    @OneToMany(mappedBy = "helper")
    @JsonIgnore
    private  List<Helping> helpingList;

    @OneToMany(mappedBy = "comment")
    @JsonIgnore
    private  List<Helping> commented;
}
