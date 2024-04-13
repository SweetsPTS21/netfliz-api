package com.netfliz.netfliz.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IBaseRepository<Entity, String> extends MongoRepository<Entity, String>{
}
