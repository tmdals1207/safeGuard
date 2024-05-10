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
@Table(name = "child")
public class Child {
    @Id
    private String childName;

    private String childPassword;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    private float latitude;
    private float longitude;

    @OneToMany(mappedBy = "child", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Parenting> parentingList;

    @OneToMany(mappedBy = "child", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Helping> helpingList;

    @OneToMany(mappedBy = "child", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Coordinate> livingAreas;

    @OneToMany(mappedBy = "child", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Coordinate> forbiddenAreas;

}
