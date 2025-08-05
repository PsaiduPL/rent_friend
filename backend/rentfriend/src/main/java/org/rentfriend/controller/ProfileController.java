package org.rentfriend.controller;


import org.rentfriend.dto.BodyParameterDTO;
import org.rentfriend.dto.ProfileDTO;
import org.rentfriend.entity.MyUser;
import org.rentfriend.entity.Profile;
import org.rentfriend.exception.ProfileAlreadyExistsException;
import org.rentfriend.exception.ProfileNotFoundException;
import org.rentfriend.repository.ProfileRepository;
import org.rentfriend.repository.UserRepository;
import org.rentfriend.requestData.ProfileRequest;
import org.rentfriend.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/profile")
public class ProfileController {

  final ProfileService profileService;
  final ProfileRepository profileRepository;
  final UriComponentsBuilder ucb;
  ProfileController(ProfileService profileService,
                    ProfileRepository profileRepository
  , UriComponentsBuilder ucb) {
    this.profileService = profileService;
    this.profileRepository = profileRepository;
    this.ucb = ucb;
  }


  @PostMapping()
  ResponseEntity<Void> createProfile(@RequestBody ProfileRequest profileRequest
  , Principal principal) {

    ProfileDTO profile =profileService.createProfile(profileRequest, principal);
    URI uri = ucb.path("/profile/{id}")
        .buildAndExpand(profile.id())
        .toUri();
    return ResponseEntity.created(uri).build();

  }
  @GetMapping("/{id}")
  ResponseEntity<ProfileDTO> getProfile(@PathVariable Long id) {

    return ResponseEntity.ok(profileService.findProfileById(id));
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
  public record ErrorResponse(int status, String message) {
  }

}
