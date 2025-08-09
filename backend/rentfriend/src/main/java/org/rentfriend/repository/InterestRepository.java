package org.rentfriend.repository;

import org.rentfriend.entity.Interest;
import org.springframework.context.annotation.ReflectiveScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestRepository extends JpaRepository<Interest,Long> {

  @Query(value = "SELECT i FROM Interest i",countQuery = "SELECT COUNT(i) FROM Interest i")
  Page<Interest> getAllWithoutAdditionalData(Pageable pageable);

}
