package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.MovieEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface IMovieRepository extends IBaseRepository<MovieEntity, String>{
}
