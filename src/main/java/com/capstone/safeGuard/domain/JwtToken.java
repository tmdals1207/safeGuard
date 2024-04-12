package com.capstone.safeGuard.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class JwtToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;
    private String grantType;
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private boolean isBlackList = false;

    @Builder
    public JwtToken(String grantType, String accessToken, String refreshToken) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
