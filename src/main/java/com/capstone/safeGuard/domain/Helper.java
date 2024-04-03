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
@Table(name="helper")

public class Helper {
    @Id
    private String helperId;

    @OneToOne
    @MapsId
    @JsonIgnore
    private Member member;

    @OneToMany(mappedBy = "helper")
    @JsonIgnore
    private List<Helping> helpingList;

}
