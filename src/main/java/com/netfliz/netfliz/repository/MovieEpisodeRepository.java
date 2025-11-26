package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.MovieEpisodeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieEpisodeRepository extends JpaRepository<MovieEpisodeEntity, Long> {
    Page<MovieEpisodeEntity> findByMovieId(Long movieId, Pageable pageable);
}
