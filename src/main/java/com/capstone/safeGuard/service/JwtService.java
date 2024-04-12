package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.JwtToken;
import com.capstone.safeGuard.dto.TokenInfo;
import com.capstone.safeGuard.repository.JwtTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtTokenRepository jwtTokenRepository;

    public void storeToken(TokenInfo tokenInfo) {
        jwtTokenRepository.save(
                JwtToken.builder()
                        .grantType(tokenInfo.getGrantType())
                        .accessToken(tokenInfo.getAccessToken())
                        .refreshToken(tokenInfo.getRefreshToken()).build()
        );
    }


}
