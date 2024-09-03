package com.projectx.authentication_service.service;

import com.projectx.authentication_service.entity.TokenDetails;
import com.projectx.authentication_service.payloads.AuthResponse;
import com.projectx.authentication_service.payloads.LoginResponse;

import java.text.ParseException;

public interface TokenService {
    LoginResponse getTokenDetails(String username) throws ParseException;
    TokenDetails saveRefreshToken(String username) throws ParseException;
    TokenDetails validateRefreshToken(TokenDetails token, String username) throws ParseException;
    Boolean isRefreshTokenExpiry(TokenDetails tokenDetails) throws ParseException;
    Boolean isRefreshTokenExist(String token);
    AuthResponse getRefreshToken(String username) throws ParseException;
}
