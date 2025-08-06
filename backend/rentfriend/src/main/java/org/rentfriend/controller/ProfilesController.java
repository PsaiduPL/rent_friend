package org.rentfriend.controller;


import org.rentfriend.dto.ProfileDTO;
import org.rentfriend.dto.ProfileDetailsDTO;
import org.rentfriend.repository.ProfileRepository;
import org.rentfriend.repository.UserRepository;
import org.rentfriend.service.OfferService;
import org.rentfriend.service.ProfileService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/profiles")
public class ProfilesController {
  final UserRepository userRepository;
  final ProfileService profileService;
  final OfferService offerService;
  final UriComponentsBuilder ucb;

  ProfilesController(ProfileService profileService,
                    ProfileRepository profileRepository,
                    UriComponentsBuilder ucb,
                    UserRepository userRepository,
                    OfferService offerService) {
    this.profileService = profileService;
    this.offerService = offerService;
    this.ucb = ucb;
    this.userRepository = userRepository;
  }
  @GetMapping()
  ResponseEntity<List<ProfileDTO>> getProfiles(Pageable pageable) {

    return ResponseEntity.ok(profileService.getAllSellerProfiles(pageable));
  }

  @Transactional
  @GetMapping("/{id}")
  ResponseEntity<ProfileDetailsDTO> getProfile(@PathVariable Long id) {

    return ResponseEntity.ok(new ProfileDetailsDTO(profileService.findProfileById(id), offerService.findOffersByProfileId(id)));
  }


}
