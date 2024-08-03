package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.ProfileEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProfileRepository extends IBaseRepository<ProfileEntity, String>{
    @Query("{ 'userId' : ?0 }")
    Optional<List<ProfileEntity>> findByUserId(String userId);

    @Query("{ 'userId' : ?0, 'profileId' : ?1 }")
    Optional<ProfileEntity> findByUserIdAndProfileId(String userId, String profileId);
}
