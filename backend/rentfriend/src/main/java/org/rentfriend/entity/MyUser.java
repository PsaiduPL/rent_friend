package org.rentfriend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;

import java.sql.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class MyUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false, unique = true, name = "username"
  )
  @NotBlank
  @Size(min = 1, max = 50)
  String username;

  @Column(nullable = false, unique = false, name = "email"
  )
  @Size(min = 1, max = 100)
  @NotBlank
  @Email
  String email;

  @Column(name = "password", nullable = false
  )
  @NotBlank
  String password;

  @Column(name = "role", nullable = false

  )
  @Size(min = 1, max = 20)
  @NotBlank

  String role;

  //@JsonIgnore
  @OneToOne(mappedBy = "user",fetch =  FetchType.LAZY)
  Profile profile;

  @Column(name = "creation_date",insertable = false)
  @Temporal(TemporalType.DATE)
  Date creationDate;

}
