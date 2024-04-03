package com.capstone.safeGuard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name="helping")
public class Helping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long helpingId;

    @ManyToOne
    private Helper helper;

    @ManyToOne
    private Child child;
}
