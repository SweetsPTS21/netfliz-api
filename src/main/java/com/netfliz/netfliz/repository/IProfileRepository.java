package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IProfileRepository extends JpaRepository<ProfileEntity, Long> {

    Optional<List<ProfileEntity>> findByUserId(String userId);

    @Query(value = """
            select p from ProfileEntity p
            where p.userId = :id and p.id = :profileId
            """)
    Optional<ProfileEntity> findByUserIdAndProfileId(Long id, Long profileId);
}
