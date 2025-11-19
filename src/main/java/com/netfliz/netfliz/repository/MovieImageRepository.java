package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.MovieImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieImageRepository extends JpaRepository<MovieImage, Integer> {
}
