package org.rentfriend.repository;

import jakarta.persistence.NamedQuery;
import org.rentfriend.entity.Interest;
import org.rentfriend.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile,Long>, JpaSpecificationExecutor<Profile> {
  @EntityGraph(attributePaths = {"user","bodyParameter","interestList"})
  Optional<Profile> findProfileById(Long id);
  @EntityGraph(attributePaths = {"user","bodyParameter","interestList"})
  Optional<Profile> findProfileByUser_Username(String name);
  @EntityGraph(attributePaths = {"user","interestList","bodyParameter"})
  Optional<Profile> findProfileByUser_Id(Long id);

  boolean existsById(Long id);
  @EntityGraph(attributePaths = {"offerList","user","bodyParameter"})
  List<Profile> findAll( Specification<Profile> spec);

  Optional<Profile> findByIdAndUser_Id(Long Id,long userId);


  @Query("SELECT p.interestList FROM Profile p WHERE p.id = ?1")
  List<Interest> findInterestListById(Long id);

  @Query(value = """
    SELECT DISTINCT p
    FROM Profile p
    LEFT JOIN FETCH p.interestList
    WHERE p IN :profiles
       
    """, countQuery = """
        SELECT COUNT(DISTINCT p)
        FROM Profile p
        WHERE p IN :profiles
    """)
  Page<Profile> findProfilesWithInterests(@Param("profiles") List<Profile> profiles,Pageable pageable);

}
