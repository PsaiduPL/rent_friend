package org.rentfriend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "images")
public class ImageDB {

  @Id
 // @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  String url;

  @JsonIgnore
  @OneToOne( fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_id", referencedColumnName = "id",nullable = false, insertable = true, updatable = false)
  Profile profile;
}
