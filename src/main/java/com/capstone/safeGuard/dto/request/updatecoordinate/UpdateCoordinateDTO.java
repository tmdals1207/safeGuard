package com.capstone.safeGuard.dto.request.updatecoordinate;

import lombok.Getter;

@Getter
public class UpdateCoordinateDTO {
    private String type;
    private String id;
    private float latitude;
    private float longitude;
}
