package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.MovieImageEntity;
import com.netfliz.netfliz.entity.enums.MovieImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface MovieImageRepository extends JpaRepository<MovieImageEntity, Long> {

    @Query("SELECT e FROM MovieImageEntity e WHERE e.movieId IN :movieIds")
    List<MovieImageEntity> getAllByMovieIds(Collection<Long> movieIds);

    List<MovieImageEntity> findByMovieId(Long movieId);

    List<MovieImageEntity> findByMovieIdAndImageTypeIn(Long movieId, Collection<MovieImageType> imageTypes);
}
