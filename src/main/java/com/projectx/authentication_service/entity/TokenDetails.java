package com.projectx.authentication_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "token_details")
public class TokenDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "token_id")
    private String tokenId;
    @Column(name = "expiry_date")
    private Date expiryDate;
    @Column(name = "access_token",length = 1500)
    private String accessToken;
    @Column(name = "refresh_token",length = 1500)
    private String refreshToken;
    @Column(name = "user_id")
    private Long userId;
}
