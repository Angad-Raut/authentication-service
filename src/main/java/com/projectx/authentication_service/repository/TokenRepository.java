package com.projectx.authentication_service.repository;

import com.projectx.authentication_service.entity.TokenDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenDetails,Long> {
    @Query(value = "select * from token_details where id=:id",nativeQuery = true)
    TokenDetails getById(@Param("id") Long id);
    TokenDetails findTokenDetailsByTokenId(String tokenId);
    TokenDetails findTokenDetailsByUserId(Long userId);
}
