package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFileRepository extends JpaRepository<FileEntity, Long> {
}
