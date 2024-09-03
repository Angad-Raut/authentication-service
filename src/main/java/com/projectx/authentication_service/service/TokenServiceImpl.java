package com.projectx.authentication_service.service;

import com.projectx.authentication_service.entity.TokenDetails;
import com.projectx.authentication_service.entity.Users;
import com.projectx.authentication_service.exceptions.ResourceNotFoundException;
import com.projectx.authentication_service.payloads.AuthResponse;
import com.projectx.authentication_service.payloads.LoginResponse;
import com.projectx.authentication_service.payloads.TokenDto;
import com.projectx.authentication_service.repository.TokenRepository;
import com.projectx.authentication_service.utils.UserConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
public class TokenServiceImpl implements TokenService{

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public LoginResponse getTokenDetails(String username) throws ParseException {
        Users users = userService.getUserByUserName(username);
        if (users == null) {
            throw new ResourceNotFoundException(UserConstant.USER_DETAILS_NOT_EXIST);
        } else {
            TokenDto tokenDetails = jwtService.generateTokenTwo(username);
            return LoginResponse.builder()
                    .userId(users.getUserId())
                    .userRole(users.getUserRole())
                    .userName(users.getUserName())
                    .accessToken(tokenDetails.getToken())
                    .expiryDate(tokenDetails.getExpiryDate())
                    .build();
        }
    }

    @Override
    public TokenDetails saveRefreshToken(String username) throws ParseException {
        Users users = userService.getUserByUserName(username);
        if (users == null) {
            throw new ResourceNotFoundException(UserConstant.USER_DETAILS_NOT_EXIST);
        } else {
            TokenDetails fetchData = tokenRepository.findTokenDetailsByUserId(users.getUserId());
            if (fetchData==null) {
                Instant futureDate = Instant.ofEpochSecond(Instant.now().plus(UserConstant.refreshTokenExpirationDays, ChronoUnit.DAYS).getEpochSecond());
                String convertedDate = formatter.format(Date.from(Date.from(futureDate).toInstant()));
                TokenDetails tokenData = TokenDetails.builder()
                        .tokenId(UUID.randomUUID().toString())
                        .expiryDate(formatter.parse(convertedDate))
                        .userId(users.getUserId())
                        .build();
                String accessToken = jwtService.generateToken(username);
                String refreshToken = jwtService.generateRefreshToken(users, tokenData);
                tokenData.setAccessToken(accessToken);
                tokenData.setRefreshToken(refreshToken);
                return tokenRepository.save(tokenData);
            } else {
                validateRefreshToken(fetchData,username);
                return fetchData;
            }
        }
    }
    @Override
    public TokenDetails validateRefreshToken(TokenDetails token, String username) throws ParseException {
        if (isRefreshTokenExpiry(token) && isRefreshTokenExist(token.getTokenId())) {
            //throw new TokenExpiryedException(Constants.REFRESH_TOKEN_EXPIRED);
            return createRefreshToken(username);
        } else {
            return token;
        }
    }

    @Override
    public Boolean isRefreshTokenExpiry(TokenDetails tokenDetails) throws ParseException {
        Instant nowDate = Instant.ofEpochSecond(Instant.now().getEpochSecond());
        String convertedDate = formatter.format(Date.from(Date.from(nowDate).toInstant()));
        Date currentDate = formatter.parse(convertedDate);
        if (tokenDetails.getExpiryDate().compareTo(currentDate)<0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean isRefreshTokenExist(String token) {
        TokenDetails tokenDetails = tokenRepository.findTokenDetailsByTokenId(token);
        if (tokenDetails != null) {
            return true;
        } else {
            return false;
        }
    }

    private TokenDetails createRefreshToken(String username) throws ParseException {
        Users users = userService.getUserByUserName(username);
        if (users == null) {
            throw new ResourceNotFoundException(UserConstant.USER_DETAILS_NOT_EXIST);
        } else {
            TokenDetails refreshTokenDetails = tokenRepository.findTokenDetailsByUserId(users.getUserId());
            if (refreshTokenDetails != null) {
                tokenRepository.delete(refreshTokenDetails);
                Instant futureDate = Instant.ofEpochSecond(Instant.now().plus(UserConstant.refreshTokenExpirationDays, ChronoUnit.DAYS).getEpochSecond());
                String convertedDate = formatter.format(Date.from(Date.from(futureDate).toInstant()));
                TokenDetails requestToken = TokenDetails.builder()
                        .tokenId(UUID.randomUUID().toString())
                        .expiryDate(formatter.parse(convertedDate))
                        .userId(users.getUserId())
                        .build();
                String accessToken = jwtService.generateToken(username);
                String refreshToken = jwtService.generateRefreshToken(users, requestToken);
                requestToken.setAccessToken(accessToken);
                requestToken.setRefreshToken(refreshToken);
                refreshTokenDetails = tokenRepository.save(requestToken);
            }
            return refreshTokenDetails;
        }
    }

    @Override
    public AuthResponse getRefreshToken(String username) throws ParseException {
        Users users = userService.getUserByUserName(username);
        if (users == null) {
            throw new ResourceNotFoundException(UserConstant.USER_DETAILS_NOT_EXIST);
        } else {
            TokenDetails refreshTokenDetails = tokenRepository.findTokenDetailsByUserId(users.getUserId());
            if (refreshTokenDetails != null) {
                tokenRepository.delete(refreshTokenDetails);
                Instant futureDate = Instant.ofEpochSecond(Instant.now().plus(UserConstant.refreshTokenExpirationDays, ChronoUnit.DAYS).getEpochSecond());
                String convertedDate = formatter.format(Date.from(Date.from(futureDate).toInstant()));
                TokenDetails requestToken = TokenDetails.builder()
                        .tokenId(UUID.randomUUID().toString())
                        .expiryDate(formatter.parse(convertedDate))
                        .userId(users.getUserId())
                        .build();
                String accessToken = jwtService.generateToken(username);
                String refreshToken = jwtService.generateRefreshToken(users,requestToken);
                requestToken.setAccessToken(accessToken);
                requestToken.setRefreshToken(refreshToken);
                refreshTokenDetails =tokenRepository.save(requestToken);
            }
            return AuthResponse.builder()
                    .userRole(users.getUserRole())
                    .userName(users.getUserName())
                    .refreshTokenId(refreshTokenDetails.getTokenId()!=null?refreshTokenDetails.getTokenId():null)
                    .accessToken(refreshTokenDetails.getAccessToken()!=null?refreshTokenDetails.getAccessToken():null)
                    .refreshToken(refreshTokenDetails.getRefreshToken()!=null?refreshTokenDetails.getRefreshToken():null)
                    .build();
        }
    }
}
