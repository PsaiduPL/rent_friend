package org.rentfriend.repository;

import jdk.jfr.Registered;
import org.rentfriend.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer,Long> {
  Optional<Offer> findByIdAndProfile_Id(Long id, Long profile_id);
}
