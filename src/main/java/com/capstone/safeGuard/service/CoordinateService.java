package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Coordinate;
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

    @Transactional
    public boolean addLivingArea(AddAreaDTO addAreaDTO){
        Optional<Child> foundChild = childRepository.findById(addAreaDTO.getChildId());
        if(foundChild.isEmpty()){
            return false;
        }

        foundChild.get().getForbiddenAreas()
                .add(addAreaDTO.dtoToDomain(foundChild.get(), true));

        return true;
    }

    @Transactional
    public boolean deleteArea(Long childId, Long coordinateId){
        Optional<Child> foundChild = childRepository.findById(childId);
        Optional<Coordinate> foundCoordinate = coordinateRepository.findById(coordinateId);
        if(foundChild.isEmpty() ||
                foundCoordinate.isEmpty() ||
                (foundCoordinate.get().getChild() != foundChild.get()) ){
            return false;
        }

        coordinateRepository.delete(foundCoordinate.get());
        return true;
    }

}
