package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.JwtToken;
import com.capstone.safeGuard.dto.TokenInfo;
import com.capstone.safeGuard.repository.JwtTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

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


    public void toBlackList(String accessToken) {
        Optional<JwtToken> findToken = jwtTokenRepository.findByAccessToken(accessToken);

        if(findToken.isEmpty() || findToken.get().isBlackList()){
            throw new NoSuchElementException("Access token does not exist");
        }

        findToken.get().setBlackList(true);
    }
}
