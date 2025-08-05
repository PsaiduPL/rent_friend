package org.rentfriend.repository;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.rentfriend.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<MyUser,Long> {

  Optional<MyUser> findMyUserByUsername(String username);
  MyUser findTopMyUserByUsername(String username);
  Optional<MyUser> findMyUserByUsernameOrEmail(@NotBlank @Size(min = 1, max = 50) String username,String email);

  Optional<MyUser> findTopByUsernameOrEmailAndRole(String username, String email, String role);
}
