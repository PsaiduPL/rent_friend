package org.rentfriend.controller;


import org.rentfriend.entity.Interest;
import org.rentfriend.repository.InterestRepository;
import org.rentfriend.service.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
  InterestService interestService;

  public InterestController(InterestService interestService) {
    this.interestService = interestService;
  }


  @GetMapping()
  public ResponseEntity<InterestResponse> getAllInterests(Pageable pageable) {

    return ResponseEntity.ok(interestService.getAllInterestsList(pageable));
  }

  public record InterestResponse(int pages, int pageSize,List<Interest> interests) {
  }
}
