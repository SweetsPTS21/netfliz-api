package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.ProfileEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface IProfileRepository extends IBaseRepository<ProfileEntity, String>{
}
