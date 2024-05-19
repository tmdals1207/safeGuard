package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.dto.request.LocationDto;
import com.capstone.safeGuard.service.FcmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Random;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/tracking")
public class TrackingLocationController {

    private final FcmService fcmService;
    private static final Random random = new Random();
    @PostMapping("/location")
    public ResponseEntity sendLocation(@RequestBody @Valid LocationDto dto){
        String childUserName = getUserName();
        if (childUserName ==null){
            return ResponseEntity.status(403).build();
        }
        // logic check location of child not ok ?
        boolean isLocationNotOk = needNotify(dto.getLatLocate(),dto.getLongLocate());
        if (isLocationNotOk){
            fcmService.sendNoticeReport(childUserName);
        }
        return ResponseEntity.ok("Success");
    }

    //notify call 경우
    private boolean needNotify(double latLocate, double longLocate) {
        return random.nextBoolean();
    }

    private String getUserName (){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }
}
