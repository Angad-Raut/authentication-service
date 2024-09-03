package com.projectx.authentication_service.repository;

import com.projectx.authentication_service.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {
    @Query(value = "select * from user_details where user_id=:userId",nativeQuery = true)
    Users getUserById(@Param("userId") Long userId);
    Users findByUserMobile(Long mobile);
    Users findByUserEmail(String email);

    Boolean existsByUserMobile(Long mobile);
    Boolean existsByUserEmail(String email);
}
