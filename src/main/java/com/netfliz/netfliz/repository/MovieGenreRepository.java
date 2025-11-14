package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.MovieGenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieGenreRepository extends JpaRepository<MovieGenreEntity, Integer> {
}
