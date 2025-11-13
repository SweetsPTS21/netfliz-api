package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.MovieEntity;
import com.netfliz.netfliz.model.request.MovieFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomMovieRepository {
    Page<MovieEntity> findByGenres(String[] genres, Pageable pageable);

    Page<MovieEntity> findByFilter(MovieFilterRequest request);
}
