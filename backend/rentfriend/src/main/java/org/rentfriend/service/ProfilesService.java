package org.rentfriend.service;


import org.rentfriend.dto.InterestDTO;
import org.rentfriend.dto.ProfileDTO;
import org.rentfriend.entity.Offer;
import org.rentfriend.preview.OfferPreview;
import org.rentfriend.preview.ProfilePreview;
import org.rentfriend.entity.Profile;
import org.rentfriend.repository.ProfileRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class ProfilesService {


  final ProfileRepository profileRepository;
  final UriComponentsBuilder ucb;

  public ProfilesService(ProfileRepository profileRepository,
                        UriComponentsBuilder ucb) {

    this.profileRepository = profileRepository;
    this.ucb = ucb;

  }
  @Cacheable(
      value = "cached-profiles",
      key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()",
      unless = "#result == null"
  )
  @Transactional
  public List<ProfilePreview> getAllSellerProfiles(Pageable pageable) {
    System.out.println("wykonuje zapytanie do bazy profiles");
    Page<Profile> profiles =  profileRepository.findProfilesByUser_role("SELLER", PageRequest.of(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        pageable.getSort()));
    return profiles.getContent().parallelStream().map(a->mapProfilePreview(a)).toList();
  }

  ProfilePreview mapProfilePreview(Profile profile) {
    return  new ProfilePreview(profile.getId(),
        profile.getUser().getRole(),//TODO przeanalizuje ten kod za duzo fetchowania
        profile.getName(),
        profile.getAge(),
        profile.getCity(),
        profile.getGender(),
        profile.getInterestList()
            .subList(0, Math.min(profile.getInterestList().size(), 3)).stream().map(a->new InterestDTO(a.getId(),a.getInterest())).toList(),
        profile.getOfferList().subList(0, Math.min(profile.getOfferList().size(), 3)).stream().map(this::mapOfferPreview).toList());
  }

  OfferPreview mapOfferPreview(Offer offer) {
    return new OfferPreview(offer.getTitle(),offer.getPricePerHour().doubleValue());
  }
}
