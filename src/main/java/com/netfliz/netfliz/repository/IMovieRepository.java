package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.MovieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IMovieRepository extends JpaRepository<MovieEntity, Long> {

    Page<MovieEntity> findAll(Specification<MovieEntity> specification, Pageable pageable);

//    @Query("SELECT m FROM MovieEntity m WHERE :criteria")
//    Page<MovieEntity> findTopRatedMovies(@Param("criteria") String criteria, Pageable pageable);

    @Query("SELECT m FROM MovieEntity m WHERE :fieldName = :value")
    Page<MovieEntity> findAllByFieldName(@Param("fieldName") String fieldName, @Param("value") String value, Pageable pageable);
}
