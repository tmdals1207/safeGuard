package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.dto.request.coordinate.AddAreaDTO;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.CoordinateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoordinateService {
    private final CoordinateRepository coordinateRepository;
    private final ChildRepository childRepository;

    @Transactional
    public boolean addForbiddenArea(AddAreaDTO addAreaDTO){
        Optional<Child> foundChild = childRepository.findById(addAreaDTO.getChildId());
        if(foundChild.isEmpty()){
            return false;
        }

        foundChild.get().getForbiddenAreas()
                .add(addAreaDTO.dtoToDomain(foundChild.get(), false));
        return true;
    }

    // TODO 해당 child의 위험지역 삭제
    public boolean deleteForbiddenArea(AddAreaDTO addAreaDTO){
        return true;
    }

    public boolean addLivingArea(AddAreaDTO addAreaDTO){
        Optional<Child> foundChild = childRepository.findById(addAreaDTO.getChildId());
        if(foundChild.isEmpty()){
            return false;
        }

        foundChild.get().getForbiddenAreas()
                .add(addAreaDTO.dtoToDomain(foundChild.get(), true));

        return true;
    }

    // TODO 해당 child의 안전지역 삭제
    public boolean deleteLivingArea(AddAreaDTO addAreaDTO){
        return true;
    }

}
