package org.rentfriend.controller;


import org.rentfriend.entity.Interest;
import org.rentfriend.repository.InterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/interests")
public class InterestController {
  InterestRepository interestRepository;

  @Autowired
  public InterestController(InterestRepository interestRepository) {
    this.interestRepository = interestRepository;
  }


  @GetMapping()
  ResponseEntity<InterestResponse> getAllInterests(Pageable pageable) {
    Page<Interest> persons = interestRepository.findAll(PageRequest.of(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        pageable.getSort()
    ));

    return ResponseEntity.ok(new InterestResponse(persons.getTotalPages(),persons.getSize(),persons.getContent()));
  }
  public record InterestResponse(int pages, int pageSize,List<Interest> interests) {
  }
}
