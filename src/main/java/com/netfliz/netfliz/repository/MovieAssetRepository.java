package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.MovieAssetEntity;
import com.netfliz.netfliz.entity.enums.MovieAssetType;
import com.netfliz.netfliz.entity.enums.MovieObjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface MovieAssetRepository extends JpaRepository<MovieAssetEntity, Long> {

    @Query("SELECT a FROM MovieAssetEntity a WHERE a.objectId = :movieId AND a.objectType = :objectType")
    List<MovieAssetEntity> findByObjectId(Long movieId, MovieObjectType objectType);

    @Query("SELECT a FROM MovieAssetEntity a WHERE a.objectId IN :objectIds AND a.objectType = :objectType")
    List<MovieAssetEntity> findByObjectIds(Collection<Long> objectIds, MovieObjectType objectType);

    @Modifying
    @Query("DELETE FROM MovieAssetEntity a WHERE a.objectId = :episodeId AND a.objectType = :objectType AND a.assetType IN :assetTypes")
    void deleteAllByObjectId(Long episodeId, MovieObjectType objectType, Collection<MovieAssetType> assetTypes);
}
