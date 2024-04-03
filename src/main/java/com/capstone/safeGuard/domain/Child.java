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
@Table(name="child")
public class Child {
    @Id
    private String child_id;
    private String child_name;
    private String child_password;

    @OneToMany(mappedBy = "child")
    @JsonIgnore
    private List<Helping> helpingList;

    @OneToMany(mappedBy = "child")
    @JsonIgnore
    private List<Coordinate> coordinateList;

    @OneToMany(mappedBy = "child")
    @JsonIgnore
    private List<Confirm> confirmList;

    @OneToMany(mappedBy = "child")
    @JsonIgnore
    private List<Notice> noticeList;








}
