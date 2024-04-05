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
@Table(name="helper")

public class Helper extends Member{
    @Id
    private String helperId;

    @OneToMany(mappedBy = "helper")
    @JsonIgnore
    private List<Helping> helpingList;

}
