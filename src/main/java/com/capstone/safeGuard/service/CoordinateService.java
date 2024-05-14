package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Coordinate;
import com.capstone.safeGuard.dto.request.coordinate.AddAreaDTO;
import com.capstone.safeGuard.dto.request.coordinate.DeleteAreaDTO;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.CoordinateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoordinateService {
    private final CoordinateRepository coordinateRepository;
    private final ChildRepository childRepository;

    @Transactional
    public boolean addForbiddenArea(AddAreaDTO addAreaDTO){
        Child foundChild = childRepository.findBychildName(addAreaDTO.getChildName());
        if(foundChild == null){
            return false;
        }

        foundChild.getForbiddenAreas()
                .add(addAreaDTO.dtoToDomain(foundChild, false));

        log.info("addForbiddenArea 성공 ");
        return true;
    }

    @Transactional
    public boolean addLivingArea(AddAreaDTO addAreaDTO){
        Child foundChild = childRepository.findBychildName(addAreaDTO.getChildName());
        if(foundChild == null){
            return false;
        }

        foundChild.getForbiddenAreas()
                .add(addAreaDTO.dtoToDomain(foundChild, true));

        log.info("addLivingArea 성공 ");
        return true;
    }

    @Transactional
    public boolean deleteArea(DeleteAreaDTO dto){
        String areaID = dto.getAreaID();
        String childName = dto.getChildName();

        Child foundChild = childRepository.findBychildName(childName);
        Optional<Coordinate> foundCoordinate = coordinateRepository.findById(Long.parseLong(areaID));
        if( (foundChild == null) ||
                foundCoordinate.isEmpty() ||
                ( foundChild.equals(foundCoordinate.get().getChild()) ) ){
            return false;
        }

        coordinateRepository.delete(foundCoordinate.get());
        return true;
    }

    public ArrayList<Coordinate> readAreasByChild(String childName) {
        Child foundChild = childRepository.findBychildName(childName);

        ArrayList<Coordinate> foundCoordinates = coordinateRepository.findAllByChild(foundChild);
        if(foundCoordinates.isEmpty()){
            return null;
        }
        return foundCoordinates;
    }
}
