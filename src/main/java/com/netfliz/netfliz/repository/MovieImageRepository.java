package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.MovieImageEntity;
import com.netfliz.netfliz.entity.enums.MovieImageObjectType;
import com.netfliz.netfliz.entity.enums.MovieImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface MovieImageRepository extends JpaRepository<MovieImageEntity, Long> {

    @Query("SELECT e FROM MovieImageEntity e WHERE e.objectId IN :objectIds AND e.objectType = :objectType")
    List<MovieImageEntity> findByObjectIdsAndObjectType(Collection<Long> objectIds, MovieImageObjectType objectType);

    @Query("SELECT e FROM MovieImageEntity e WHERE e.objectId = :objectId AND e.objectType = :objectType")
    List<MovieImageEntity> findByObjectIdAndObjectType(Long objectId, MovieImageObjectType objectType);

    @Query("SELECT e FROM MovieImageEntity e WHERE e.objectId = :objectId AND e.objectType = :objectType AND e.imageType IN :imageTypes")
    List<MovieImageEntity> findByObjectIdAndObjectTypeAndImageTypeIn(Long objectId, MovieImageObjectType objectType, Collection<MovieImageType> imageTypes);

    void deleteAllByObjectIdInAndObjectType(Collection<Long> objectIds, MovieImageObjectType objectType);

    void deleteAllByObjectIdAndObjectTypeAndImageTypeIn(Long objectId, MovieImageObjectType objectType, Collection<MovieImageType> imageTypes);
}
