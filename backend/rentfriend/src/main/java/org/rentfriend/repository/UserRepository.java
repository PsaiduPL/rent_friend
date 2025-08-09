package org.rentfriend.repository;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.rentfriend.entity.MyUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<MyUser,Long> {
  @EntityGraph(attributePaths = {"profile","profile.bodyParameter"})
  Optional<MyUser> findMyUserByUsername(String username);
  @Query("SELECT u FROM MyUser u WHERE u.username = :username")
  Optional<MyUser> findUserByUsername(@Param("username")String username);
  MyUser findTopMyUserByUsername(String username);
  Optional<MyUser> findMyUserByUsernameOrEmail(@NotBlank @Size(min = 1, max = 50) String username,String email);

  Optional<MyUser> findTopByUsernameOrEmailAndRole(String username, String email, String role);

  @Query("SELECT u.id FROM MyUser u WHERE u.username = ?1")
  Long getUserIdByUsername(String username);
}
