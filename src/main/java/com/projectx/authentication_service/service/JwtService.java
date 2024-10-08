package com.projectx.authentication_service.service;

import com.projectx.authentication_service.entity.TokenDetails;
import com.projectx.authentication_service.entity.Users;
import com.projectx.authentication_service.payloads.TokenDto;
import com.projectx.authentication_service.utils.UserConstant;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {

    @Value("${app.jwt.secret}")
    private String SECRET;

    @Autowired
    private UserService userService;

    public String generateToken(String username) {
        Map<String,Object> claims=new HashMap<>();
        return createToken(claims,username);
    }
    public TokenDto generateTokenTwo(String username) {
        Map<String,Object> claimsData=new HashMap<>();
        String token = createToken(claimsData,username);
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return TokenDto.builder()
                .token(token)
                .expiryDate(claims.getExpiration())
                .build();
    }

    private String createToken(Map<String, Object> claims, String username) {
        Users users = userService.getUserByUserName(username);
        if (users!=null) {
            claims.put("userId", users.getUserId());
            claims.put("userName", users.getUserName());
            claims.put("userEmail", users.getUserEmail());
            claims.put("userMobile", users.getUserMobile());
            claims.put("userRoles", users.getUserRole());
            if (users.getUserStatus()) {
                claims.put("userStatus", "Activate");
            } else {
                claims.put("userStatus", "Deactivate");
            }
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + UserConstant.refreshTokenExpirationMs))
                    .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
        } else {
            return "-";
        }
    }

    public String generateRefreshToken(Users user, TokenDetails refreshToken) {
        if (user!=null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("tokenId", refreshToken.getTokenId());
            claims.put("userId", user.getUserId());
            claims.put("userName", user.getUserName());
            claims.put("userEmail", user.getUserEmail());
            claims.put("userMobile", user.getUserMobile());
            claims.put("userRoles", user.getUserRole());
            if (user.getUserStatus()) {
                claims.put("userStatus", "Activate");
            } else {
                claims.put("userStatus", "Deactivate");
            }
            return Jwts.builder()
                    .setSubject(user.getUserName())
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + UserConstant.refreshTokenExpirationMs))
                    .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
        } else {
            return "-";
        }
    }

    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
        } catch (MalformedJwtException ex) {
        } catch (ExpiredJwtException ex) {
        } catch (UnsupportedJwtException ex) {
        } catch (IllegalArgumentException ex) {
        }
        return false;
    }

    public String getUserUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
