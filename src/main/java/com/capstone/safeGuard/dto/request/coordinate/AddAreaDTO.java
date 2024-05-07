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

    public Coordinate dtoToDomain(Child child, boolean isLiving){
        return Coordinate.builder()
                .child(child)
                .isLivingArea(isLiving)
                // TODO coordinate 위치 계산해서 넗기
                .build();
    }
}
