package com.capstone.safeGuard.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "coordinate")
@Builder
public class Coordinate {
    @Id
    private long coordinateId;

    @ManyToOne
    private Child child;

    private boolean isLivingArea;

    private float xOfNorthWest;
    private float yOfNorthWest;
    private float xOfNorthEast;
    private float yOfNorthEast;
    private float xOfSouthWest;
    private float yOfSouthWest;
    private float xOfSouthEast;
    private float yOfSouthEast;
}
