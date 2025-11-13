package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.ConfigEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConfigRepository extends JpaRepository<ConfigEntity, Integer> {

    @Query("SELECT ce FROM ConfigEntity ce WHERE ce.active = :active")
    Page<ConfigEntity> findConfigsByActive(Boolean active, Pageable pageable);

    Optional<ConfigEntity> findByActive(Boolean active);
}
