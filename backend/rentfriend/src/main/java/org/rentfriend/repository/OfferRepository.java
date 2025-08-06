package org.rentfriend.repository;

import jdk.jfr.Registered;
import org.rentfriend.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer,Long> {
  Optional<Offer> findByIdAndProfile_Id(Long id, Long profile_id);


  @Modifying
  @Query("UPDATE Offer o set o.title = :title,o.description = :description,o.pricePerHour = :pricePerHour WHERE o.id = :id")
  void updateOffer(@Param("id")Long id,
                   @Param("title")String title,
                   @Param("description")String description,
                   @Param("pricePerHour")Double pricePerHour);
}
