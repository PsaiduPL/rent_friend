package org.rentfriend.repository;

import org.rentfriend.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile,Long> {
  Optional<Profile> findProfileById(Long id);

  Optional<Profile> findProfileByUser_Username(String name);

  Optional<Profile> findProfileByUser_Id(Long id);

  Page<Profile> findProfilesByUser_role(String role ,Pageable pageable);

  Optional<Profile> findByIdAndUser_Id(Long Id,long userId);
}
