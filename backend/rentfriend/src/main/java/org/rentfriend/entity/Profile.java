package org.rentfriend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profiles")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Profile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "name",nullable = false)
  private String name;
  @Column(nullable = false,
      columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  String city;

  @Column(nullable = false
  ,columnDefinition = "SMALLINT")

  Short age;
  @JsonIgnore
  @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id",
      nullable = false, insertable = true, updatable = false
      , unique = true)
  MyUser user;
}
