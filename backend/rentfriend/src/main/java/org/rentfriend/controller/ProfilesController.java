package org.rentfriend.controller;


import jakarta.validation.Valid;
import org.rentfriend.dto.ProfileDTO;
import org.rentfriend.dto.ProfileDetailsDTO;
import org.rentfriend.entity.Interest;
import org.rentfriend.filter.ProfileFilterRequest;
import org.rentfriend.preview.ProfilePreview;
import org.rentfriend.repository.ProfileRepository;
import org.rentfriend.repository.UserRepository;
import org.rentfriend.service.OfferService;
import org.rentfriend.service.ProfileService;
import org.rentfriend.service.ProfilesService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/profiles")
public class ProfilesController {
  final UserRepository userRepository;
  final ProfilesService profilesService;
  final ProfileService profileService;
  final OfferService offerService;
  final UriComponentsBuilder ucb;

  ProfilesController(ProfilesService profilesService,
                     ProfileRepository profileRepository,
                     UriComponentsBuilder ucb,
                     UserRepository userRepository,
                     OfferService offerService,
                     ProfileService profileService) {
    this.profilesService = profilesService;
    this.offerService = offerService;
    this.ucb = ucb;
    this.userRepository = userRepository;
    this.profileService = profileService;
  }

  @GetMapping()
  ResponseEntity<ProfilesPreview> getProfiles(@Valid @ModelAttribute ProfileFilterRequest filter, Pageable pageable) {

    return ResponseEntity.ok(profilesService.getAllSellerProfiles(filter,pageable));
  }

  @Transactional
  @GetMapping("/{id}")
  ResponseEntity<ProfileDetailsDTO> getProfile(@PathVariable Long id) {
    ProfileDTO profile =  profileService.findProfileById(id);
    return ResponseEntity.ok(new ProfileDetailsDTO(profile, offerService.findOffersByProfileId(profile.id())));
  }


  public record ProfilesPreview(int pages, int pageSize,List<ProfilePreview> profilesPreview) {
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getBindingResult().getFieldError().getDefaultMessage());
  }

  public record ErrorResponse(int status, String message) {
  }

}
