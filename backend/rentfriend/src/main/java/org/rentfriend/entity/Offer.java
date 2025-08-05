package org.rentfriend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.util.Currency;

@Entity
@Table(name = "offers" )
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Offer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;


  @Column(name = "title", nullable = false, length = 255)
  String title;

  @Column(name = "description",nullable = false)
  String description;


  @Column(name = "price_per_hour", nullable = false, columnDefinition = "NUMERIC")
  BigDecimal pricePerHour;


  @ManyToOne(fetch =  FetchType.LAZY)
      @JoinColumn(name = "profile_id")
      @JsonIgnore
  Profile profile;

}
