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
@Table(name = "member")
public class Member {
    @Id
    private String memberId;
    private String name;
    private String password;
    private String email;


    @Enumerated(EnumType.STRING)
    private Authority authority;

    private double latitude;
    private double longitude;

    private String fcmToken;

    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    private List<Parenting> parentingList;

    @OneToMany(mappedBy = "helper")
    @JsonIgnore
    private List<Helping> helpingList;

    @OneToMany(mappedBy = "comment")
    @JsonIgnore
    private List<Comment> commented;
}
