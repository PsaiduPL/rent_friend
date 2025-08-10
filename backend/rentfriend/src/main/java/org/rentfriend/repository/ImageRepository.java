package org.rentfriend.repository;

import org.rentfriend.entity.ImageDB;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<ImageDB, UUID> {
}
