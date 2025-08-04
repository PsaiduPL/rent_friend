package org.rentfriend.repository;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.rentfriend.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<MyUser,Long> {

  Optional<MyUser> findMyUserByUsername(String username);

  Optional<MyUser> findMyUserByUsernameOrEmail(@NotBlank @Size(min = 1, max = 50) String username,String email);
}
