package org.rentfriend.repository;

import org.rentfriend.entity.ImageDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<ImageDB, UUID> {

    @Modifying
    @Query("DELETE FROM ImageDB i WHERE i.id = ?1")
    void deleteImg(UUID id);
    void deleteById(UUID id);
}
