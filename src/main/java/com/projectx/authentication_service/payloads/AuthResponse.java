package com.projectx.authentication_service.payloads;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshTokenId;
    private String refreshToken;
    private Date expiryDate;
    private String userName;
    private String userRole;
}
