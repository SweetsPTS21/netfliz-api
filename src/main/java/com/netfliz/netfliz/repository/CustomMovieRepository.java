package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.MovieEntity;
import com.netfliz.netfliz.model.request.MovieFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomMovieRepository {
    List<MovieEntity> findByGenres(List<String> genres, int limit);

    Page<MovieEntity> findByFilter(MovieFilterRequest request);
}
