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
public class Coordinate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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


    @Builder
    public Coordinate(Child child, boolean isLivingArea, float xOfNorthWest, float yOfNorthWest, float xOfNorthEast, float yOfNorthEast, float xOfSouthWest, float yOfSouthWest, float xOfSouthEast, float yOfSouthEast) {
        this.child = child;
        this.isLivingArea = isLivingArea;
        this.xOfNorthWest = xOfNorthWest;
        this.yOfNorthWest = yOfNorthWest;
        this.xOfNorthEast = xOfNorthEast;
        this.yOfNorthEast = yOfNorthEast;
        this.xOfSouthWest = xOfSouthWest;
        this.yOfSouthWest = yOfSouthWest;
        this.xOfSouthEast = xOfSouthEast;
        this.yOfSouthEast = yOfSouthEast;
    }
}
