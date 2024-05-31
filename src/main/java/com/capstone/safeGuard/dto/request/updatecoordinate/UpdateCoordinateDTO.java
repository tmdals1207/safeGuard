package com.capstone.safeGuard.dto.request.updatecoordinate;

import lombok.Getter;

@Getter
public class UpdateCoordinateDTO {
    private String type;
    private String id;
    private double latitude;
    private double longitude;
    private int battery;
}
