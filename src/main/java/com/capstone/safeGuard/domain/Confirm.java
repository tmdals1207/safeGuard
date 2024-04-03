package com.capstone.safeGuard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name="confirm")
public class Confirm {
    @Id
    private long confirmId;
    private String title;
    private String content;

    public enum cLevel{
        ARRIVED, DEPART, UNCONFIRMED
    }
    @Column(name="level")
    private cLevel clevel;

    @ManyToOne
    private  Child child;

    @ManyToOne
    private Coordinate coordinate;

    private LocalDateTime createdAt;



}
