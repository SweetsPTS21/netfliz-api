package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.UserEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends IBaseRepository<UserEntity, String>{
    @Query("{'email': ?0}")
    Optional<UserEntity> findByEmail(String email);
}
