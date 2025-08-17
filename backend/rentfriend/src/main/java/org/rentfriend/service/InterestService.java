package org.rentfriend.service;


import org.rentfriend.controller.InterestController;
import org.rentfriend.entity.Interest;
import org.rentfriend.repository.InterestRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InterestService {
  final InterestRepository interestRepository;

  public InterestService(InterestRepository interestRepository) {
    this.interestRepository = interestRepository;
  }


  @Cacheable(
      value = "cached-interests",
      key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()",
      unless = "#result == null"
  )
  public InterestController.InterestResponse getAllInterestsList(Pageable pageable) {
    // Ta linijka wykona się tylko wtedy, gdy danych nie ma w cache
    System.out.println("Wykonuję zapytanie do bazy danych...");

    var interestsPage = interestRepository.getAllWithoutAdditionalData(PageRequest.of(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        pageable.getSort()
    ));

    return new InterestController.InterestResponse(
        interestsPage.getTotalPages(),
        interestsPage.getSize(),
        interestsPage.getContent()
    );
  }

}
