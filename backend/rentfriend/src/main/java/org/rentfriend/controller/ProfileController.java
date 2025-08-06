package org.rentfriend.controller;


import jakarta.validation.Valid;
import org.rentfriend.dto.BodyParameterDTO;
import org.rentfriend.dto.ProfileDTO;
import org.rentfriend.dto.ProfileDetailsDTO;
import org.rentfriend.entity.MyUser;
import org.rentfriend.entity.Profile;
import org.rentfriend.exception.ProfileAlreadyExistsException;
import org.rentfriend.exception.ProfileNotFoundException;
import org.rentfriend.repository.ProfileRepository;
import org.rentfriend.repository.UserRepository;
import org.rentfriend.requestData.OfferRequest;
import org.rentfriend.requestData.ProfileRequest;
import org.rentfriend.service.OfferService;
import org.rentfriend.service.ProfileService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/profile")
public class ProfileController {
  final UserRepository userRepository;
  final ProfileService profileService;
  final OfferService offerService;
  final UriComponentsBuilder ucb;

  ProfileController(ProfileService profileService,
                    ProfileRepository profileRepository,
                    UriComponentsBuilder ucb,
                    UserRepository userRepository,
                    OfferService offerService) {
    this.profileService = profileService;
    this.offerService = offerService;
    this.ucb = ucb;
    this.userRepository = userRepository;
  }


  @PostMapping()
  ResponseEntity<Void> createProfile(@RequestBody @Valid ProfileRequest profileRequest
      , Principal principal) {

    ProfileDTO profile = profileService.createProfile(profileRequest, principal);
    URI uri = ucb.path("/profile/{id}")
        .buildAndExpand(profile.id())
        .toUri();
    return ResponseEntity.created(uri).build();

  }

  @GetMapping()
  ResponseEntity<List<ProfileDTO>> getProfiles(Pageable pageable) {

    return ResponseEntity.ok(profileService.getAllSellerProfiles(pageable));
  }

  @Transactional
  @GetMapping("/{id}")
  ResponseEntity<ProfileDetailsDTO> getProfile(@PathVariable Long id) {

    return ResponseEntity.ok(new ProfileDetailsDTO(profileService.findProfileById(id), profileService.findOffersByProfileId(id)));
  }

  @PostMapping("/offer")
  ResponseEntity<Void> createOffer(Principal principal, @RequestBody @Valid OfferRequest offerRequest) {
    MyUser user = userRepository.findMyUserByUsername(principal.getName()).get();

    var offer = offerService.createOffer(user.getProfile().getId(), offerRequest);
    return ResponseEntity.ok().build();

  }


  @ExceptionHandler(ProfileAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)

  public ErrorResponse handleProfileAlreadyExistsException(ProfileAlreadyExistsException ex) {
    return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
  }

  @ExceptionHandler(ProfileNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleProfileNotFoundException(ProfileNotFoundException ex) {
    return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public MyUserController.ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    return new MyUserController.ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getBindingResult().getFieldError().getDefaultMessage());
  }

  public record ErrorResponse(int status, String message) {
  }

}
