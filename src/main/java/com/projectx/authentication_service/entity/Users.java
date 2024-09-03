package com.projectx.authentication_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_details")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "user_email")
    private String userEmail;
    @Column(name = "user_mobile")
    private Long userMobile;
    @Column(name = "user_password")
    private String userPassword;
    @Column(name = "user_role")
    private String userRole;
    @Lob
    @Column(name = "photo", columnDefinition = "LONGBLOB", length = 1000)
    private byte[] photo;
    @Lob
    @Column(name = "signature", columnDefinition = "LONGBLOB", length = 1000)
    private byte[] signature;
    @Column(name = "user_status")
    private Boolean userStatus;
    @Column(name = "inserted_time")
    private Date insertedTime;
    @Column(name = "updated_time")
    private Date updatedTime;

    public Users (Users users) {
        this.userId = users.userId;
        this.userName = users.userName;
        this.userEmail = users.userEmail;
        this.userMobile = users.userMobile;
        this.userPassword = users.userPassword;
        this.userRole = users.userRole;
        this.photo = users.photo;
        this.signature = users.signature;
        this.userStatus = users.userStatus;
        this.insertedTime = users.insertedTime;
        this.updatedTime = users.updatedTime;
    }

    public List<String> getUserRoles() {
        return Collections.singletonList(userRole);
    }
}
