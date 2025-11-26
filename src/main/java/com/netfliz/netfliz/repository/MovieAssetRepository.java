package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.MovieAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieAssetRepository extends JpaRepository<MovieAssetEntity, Long> {
    List<MovieAssetEntity> findByMovieId(Long movieId);
}
