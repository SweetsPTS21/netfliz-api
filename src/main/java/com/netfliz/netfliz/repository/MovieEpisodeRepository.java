package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.MovieEpisodeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MovieEpisodeRepository extends JpaRepository<MovieEpisodeEntity, Long> {
    Page<MovieEpisodeEntity> findByMovieId(Long movieId, Pageable pageable);

    @Query("SELECT MAX(e.episodeNumber) FROM MovieEpisodeEntity e WHERE e.movieId = :movieId")
    Integer findMaxEpisodeNumberByMovieId(Long movieId);

    @Query("SELECT MAX(e.episodeOrder) FROM MovieEpisodeEntity e WHERE e.movieId = :movieId")
    Integer findMaxEpisodeOrderByMovieId(Long movieId);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM MovieEpisodeEntity e " +
            "WHERE e.movieId = :movieId AND e.episodeNumber = :episodeNumber AND e.id != :episodeId")
    Boolean existsEpisodeNumber(Long movieId, Long episodeId, Integer episodeNumber);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM MovieEpisodeEntity e " +
            "WHERE e.movieId = :movieId AND e.episodeOrder = :episodeOrder AND e.id != :episodeId")
    Boolean existsEpisodeOrder(Long movieId, Long episodeId, Integer episodeOrder);
}
