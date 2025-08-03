package org.rentfriend.repository;


import org.rentfriend.domain.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends JpaRepository<MyUser,Long> {

}
