package org.rentfriend.service;


import org.rentfriend.controller.ProfilesController;
import org.rentfriend.dto.InterestDTO;
import org.rentfriend.dto.ProfileDTO;
import org.rentfriend.entity.Offer;
import org.rentfriend.filter.ProfileFilterRequest;
import org.rentfriend.preview.OfferPreview;
import org.rentfriend.preview.ProfilePreview;
import org.rentfriend.entity.Profile;
import org.rentfriend.repository.InterestRepository;
import org.rentfriend.repository.ProfileRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfilesService {


  final ProfileRepository profileRepository;
  final UriComponentsBuilder ucb;
  final InterestRepository interestRepository;

  public ProfilesService(ProfileRepository profileRepository,
                         UriComponentsBuilder ucb,
                         InterestRepository interestRepository) {

    this.profileRepository = profileRepository;
    this.ucb = ucb;
    this.interestRepository = interestRepository;

  }

  @Cacheable(
      value = "cached-profiles",
      key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString() + '-' + #filter.city + '-' + #filter.gender + '-' + #filter.maxAge +'-'+#filter.minAge",
      unless = "#result == null"
  )
  @Transactional(readOnly = true)
  public ProfilesController.ProfilesPreview getAllSellerProfiles(ProfileFilterRequest filter, Pageable pageable) {
    System.out.println("wykonuje zapytanie do bazy profiles");
    List<Profile> profiles = profileRepository.findAll(createSpec(filter));
    Page<Profile> profilesPage = profileRepository.findProfilesWithInterests(profiles, pageable);
    return new ProfilesController.ProfilesPreview(profilesPage.getTotalPages(),
        profilesPage.getSize(), profilesPage.getContent().parallelStream().map(this::mapProfilePreview).collect(Collectors.toList())
    );
  }
  Specification<Profile> createSpec(ProfileFilterRequest filter){
    Specification<Profile> spec = Specification.unrestricted();

    if (filter.getCity() != null) {
      spec = spec.and((root, query, cb) ->cb.equal(root.get("city"),filter.getCity()));
    }

    if (filter.getGender() != null) {
      spec = spec.and((root, query, cb) ->cb.equal(root.get("gender"),filter.getGender()));;
    }

    if (filter.getMinAge() != null) {
      spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("age"), filter.getMinAge()));
    }

    if (filter.getMaxAge() != null) {
      spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("age"), filter.getMaxAge()));
    }
    spec = spec.and((root, query, cb) ->
        cb.equal(root.join("user").get("role"), "SELLER")
    )  .and((root, query, cb) -> cb.isNotNull(root.join("user").join("profile").get("id")));
    return spec;
  }
  ProfilePreview mapProfilePreview(Profile profile) {
    return new ProfilePreview(profile.getId(),
        profile.getUser().getRole(),//TODO przeanalizuje ten kod za duzo fetchowania
        profile.getName(),
        profile.getAge(),
        profile.getCity(),
        profile.getGender(),
        profile.getInterestList().stream().limit(3).map(a -> new InterestDTO(a.getId(), a.getInterest())).toList(),
        profile.getOfferList().stream().limit(3).map(this::mapOfferPreview).toList());
  }

  OfferPreview mapOfferPreview(Offer offer) {
    return new OfferPreview(offer.getTitle(), offer.getPricePerHour().doubleValue());
  }
}
