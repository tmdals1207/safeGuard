package com.capstone.safeGuard.dto.request.coordinate;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Coordinate;
import lombok.Getter;

@Getter
public class AddAreaDTO {
    private float xOfPointA;
    private float yOfPointA;
    private float xOfPointB;
    private float yOfPointB;
    private float xOfPointC;
    private float yOfPointC;
    private float xOfPointD;
    private float yOfPointD;

    private long childId;

    public Coordinate dtoToDomain(Child child, boolean isLiving) {
        float[] location = findLocation();


        return Coordinate.builder()
                .child(child)
                .isLivingArea(isLiving)

                .xOfNorthEast(location[0])
                .yOfNorthEast(location[1])
                .xOfNorthWest(location[2])
                .yOfNorthWest(location[3])
                .xOfSouthWest(location[4])
                .yOfSouthWest(location[5])
                .xOfSouthEast(location[6])
                .yOfSouthEast(location[7])

                .build();
    }

    private float[] findLocation() {
        float[] result = new float[8];

        float[][] coordinates = {
                {xOfPointA, yOfPointA},
                {xOfPointB, yOfPointB},
                {xOfPointC, yOfPointC},
                {xOfPointD, yOfPointD}
        };

        float centerX = (xOfPointA + xOfPointB + xOfPointC + xOfPointD) / 4;
        float centerY = (yOfPointA + yOfPointB + yOfPointC + yOfPointD) / 4;

        for(int i = 0; i < 4; i++){
            switch (findQuadrant(centerX, centerY, coordinates[i][0], coordinates[i][1])) {
                case 1 -> {
                    result[0] = coordinates[i][0];
                    result[1] = coordinates[i][0];
                }
                case 2 -> {
                    result[2] = coordinates[i][0];
                    result[3] = coordinates[i][0];
                }
                case 3 -> {
                    result[4] = coordinates[i][0];
                    result[5] = coordinates[i][0];
                }
                case 4 -> {
                    result[6] = coordinates[i][0];
                    result[7] = coordinates[i][0];
                }
            }
        }

        return result;
    }

    private int findQuadrant(float centerX, float centerY, float x, float y) {
        if (centerX - x < 0) {
            if (centerY - y < 0) {
                // -- sw
                return 3;
            } else {
                // -+ nw
                return 2;
            }
        } else {
            if (centerY - y < 0) {
                return 4;
                // +- se
            } else {
                return 1;
                // ++ ne
            }
        }

    }
}
