package org.rentfriend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "interests")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Interest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "interest_name", nullable = false, length = 150,unique = true)
  String interest;


  @ManyToMany(mappedBy = "interestList", fetch = FetchType.LAZY,cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
      @JsonIgnore
  List<Profile> profileList;

}
