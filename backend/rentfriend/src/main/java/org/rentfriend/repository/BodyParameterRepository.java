package org.rentfriend.repository;


import org.rentfriend.entity.BodyParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface BodyParameterRepository extends JpaRepository<BodyParameter,Long> {
}
