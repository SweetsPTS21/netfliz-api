package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.MovieAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface MovieAssetRepository extends JpaRepository<MovieAssetEntity, Long> {
    List<MovieAssetEntity> findByMovieId(Long movieId);

    List<MovieAssetEntity> findByEpisodeId(Long episodeId);

    @Query("SELECT a FROM MovieAssetEntity a WHERE a.movieId IN :movieIds AND a.episodeId = 0")
    List<MovieAssetEntity> findByMovieIds(Collection<Long> movieIds);

    void deleteAllByEpisodeId(Long episodeId);

    void deleteAllByMovieId(Long movieId);
}
