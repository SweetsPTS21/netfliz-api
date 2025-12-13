package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IFileRepository extends JpaRepository<FileEntity, Long> {
    @Query("SELECT f FROM FileEntity f WHERE f.fileName = :fileName")
    List<FileEntity> findByFileName(String fileName);
}
