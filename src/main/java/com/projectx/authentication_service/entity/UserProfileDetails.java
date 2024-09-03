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
@Table(name = "user_profile")
public class UserProfileDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String qualification;
    private String profession;
    private String gender;
    private Date dateOfBirth;
    private String bloodGroup;
    private String panNumber;
    private String aadharNumber;
    private String street;
    private String city;
    private String state;
    private String country;
    private Integer pinCode;
}
