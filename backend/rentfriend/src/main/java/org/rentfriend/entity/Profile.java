package org.rentfriend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Entity
@Table(name = "profiles")
@AllArgsConstructor
@NoArgsConstructor
@Data

@EqualsAndHashCode(exclude = "offerList")
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
  ,columnDefinition = "INTEGER")
  Integer age;

  @Column(nullable =false,
  name = "gender")
  String gender;

  @JsonIgnore
  @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id",
      nullable = false, insertable = true, updatable = false
      , unique = true)
  MyUser user;

  @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinTable(
      name = "profile_interest",
      joinColumns = {
          @JoinColumn(name = "profile_id")
      },
      inverseJoinColumns ={
          @JoinColumn(name = "interest_id")
      }

  )
  List<Interest> interestList;

  @OneToMany(mappedBy ="profile",cascade = CascadeType.ALL,fetch =  FetchType.LAZY)
  List<Offer> offerList;

  @OneToOne(mappedBy = "profile",cascade = CascadeType.ALL)
  BodyParameter bodyParameter;
}
