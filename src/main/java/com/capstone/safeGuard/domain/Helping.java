package com.capstone.safeGuard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "helping")
public class Helping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long helpingId;

    @ManyToOne
    private Member helper;

    @ManyToOne
    private Child child;

    @OneToMany(mappedBy = "confirm", cascade = CascadeType.REMOVE)
    private List<Confirm> confirms;
}
