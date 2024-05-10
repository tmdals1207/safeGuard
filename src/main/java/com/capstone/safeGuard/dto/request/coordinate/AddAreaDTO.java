package com.capstone.safeGuard.dto.request.coordinate;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Coordinate;
import lombok.Getter;

@Getter
public class AddAreaDTO {
    private double xOfPointA;
    private double yOfPointA;
    private double xOfPointB;
    private double yOfPointB;
    private double xOfPointC;
    private double yOfPointC;
    private double xOfPointD;
    private double yOfPointD;

    // TODO member마다 저장하도록 변경
    private String childName;
    private String memberID;

    public Coordinate dtoToDomain(Child child, boolean isLiving) {
        double[] location = findLocation();


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

    private double[] findLocation() {
        double[] result = new double[8];

        double[][] coordinates = {
                {xOfPointA, yOfPointA},
                {xOfPointB, yOfPointB},
                {xOfPointC, yOfPointC},
                {xOfPointD, yOfPointD}
        };

        double centerX = (xOfPointA + xOfPointB + xOfPointC + xOfPointD) / 4;
        double centerY = (yOfPointA + yOfPointB + yOfPointC + yOfPointD) / 4;

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

    private int findQuadrant(double centerX, double centerY, double x, double y) {
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