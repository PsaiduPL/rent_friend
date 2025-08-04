package org.rentfriend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Fetch;

@Entity
@Table(name = "users")
public class MyUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false,
      unique = true,
      name = "username"
  )
  @NotBlank
  @Size(min = 1, max = 50)
  String username;

  @Column(nullable = false,
      unique = false,
      name = "email"
  )
  @Size(min = 1, max = 100)
  @NotBlank
  @Email
  String email;

  @Column(
      name = "password",
      nullable = false
  )
  @NotBlank
  String password;

  @Column(
      name = "role",
      nullable = false

  )
  @Size(min = 1, max = 20)
  @NotBlank

  String role;

  //@JsonIgnore
  @OneToOne(mappedBy = "user")
  Profile profile;
  public MyUser() {
  }

  public MyUser(Long id, String username, String email, String password, String role,Profile profile) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.role = role;
    this.profile = profile;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public Profile getProfile() {
    return profile;
  }

  public void setProfile(Profile profile) {
    this.profile = profile;
  }
}
