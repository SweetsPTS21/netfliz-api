package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.MovieImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface MovieImageRepository extends JpaRepository<MovieImageEntity, Integer> {

    @Query("SELECT e FROM MovieImageEntity e WHERE e.movieId IN :movieIds")
    List<MovieImageEntity> getAllByMovieIds(Collection<Long> movieIds);

    List<MovieImageEntity> findByMovieId(Long movieId);
}
