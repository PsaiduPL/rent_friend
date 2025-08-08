package org.rentfriend.repository;

import jakarta.persistence.NamedQuery;
import org.rentfriend.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile,Long> {
  Optional<Profile> findProfileById(Long id);

  Optional<Profile> findProfileByUser_Username(String name);

  Optional<Profile> findProfileByUser_Id(Long id);


//  @Query( value = """
//      SELECT DISTINCT p FROM Profile p LEFT JOIN FETCH p.user
//          LEFT JOIN FETCH p.interestList
//          LEFT JOIN FETCH p.offerList WHERE p.user.role = :role
//      """
//      ,countQuery = "SELECT count(p) FROM Profile p WHERE p.user.role = :role")
//
//  Page<Profile> findProfilesByUserRoleWithInterest(@Param("role")String role ,Pageable pageable);

  @EntityGraph(attributePaths = {"offerList","bodyParameter","user"})
  Page<Profile> findProfilesByUser_role(@Param("role")String role ,Pageable pageable);

  Optional<Profile> findByIdAndUser_Id(Long Id,long userId);

}
