package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.MovieEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMovieRepository extends IBaseRepository<MovieEntity, String>{
}
