package com.capstone.safeGuard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "coordinate")
public class Coordinate {
    @Id
    private long coordinateId;

    @ManyToOne
    private Child child;

    private boolean isLivingArea;
    private double x;
    private double y;
}
