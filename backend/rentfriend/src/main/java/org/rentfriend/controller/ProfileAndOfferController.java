package org.rentfriend.controller;


import jakarta.validation.Valid;
import org.rentfriend.dto.ProfileDTO;
import org.rentfriend.dto.ProfileDetailsDTO;
import org.rentfriend.entity.MyUser;
import org.rentfriend.dto.OfferDTO;
import org.rentfriend.exception.OfferNotFoundException;
import org.rentfriend.exception.ProfileAlreadyExistsException;
import org.rentfriend.exception.ProfileNotFoundException;
import org.rentfriend.repository.ProfileRepository;
import org.rentfriend.repository.UserRepository;
import org.rentfriend.requestData.OfferRequest;
import org.rentfriend.requestData.ProfileRequest;
import org.rentfriend.requestData.ProfileUpdate;
import org.rentfriend.service.OfferService;
import org.rentfriend.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/profile")
public class ProfileAndOfferController {
  final UserRepository userRepository;
  final ProfileService profileService;
  final OfferService offerService;
  final UriComponentsBuilder ucb;
  private final ProfileRepository profileRepository;

  ProfileAndOfferController(ProfileService profileService,
                            ProfileRepository profileRepository,
                            UriComponentsBuilder ucb,
                            UserRepository userRepository,
                            OfferService offerService) {
    this.profileService = profileService;
    this.offerService = offerService;
    this.ucb = ucb;
    this.userRepository = userRepository;
    this.profileRepository = profileRepository;
  }


  @PostMapping()
  ResponseEntity<Void> createProfile(@RequestBody @Valid ProfileRequest profileRequest
      , Principal principal) {

    ProfileDTO profile = profileService.createProfile(profileRequest, principal);
    URI uri = ucb.path("/profiles/{id}")
        .buildAndExpand(profile.id())
        .toUri();
    return ResponseEntity.created(uri).build();

  }
  @PutMapping()
  ResponseEntity<Void> updateProfile(@RequestBody @Valid ProfileRequest profileRequest, Principal principal) {
    profileService.updateProfile(profileRequest, principal);
    return ResponseEntity.noContent().build();
  }

  @GetMapping()
  ResponseEntity<ProfileDetailsDTO> getProfile(Principal principal) {
    var user = userRepository.findTopMyUserByUsername(principal.getName());
    var profileDB = profileRepository.findProfileByUser_Id(user.getId());
    if (profileDB.isPresent()) {

      ProfileDTO profile = profileService.mapProfile(profileDB.get());
      return ResponseEntity.ok(new ProfileDetailsDTO(
              profile,
              offerService.findOffersByProfileId(
                  profile.id()
              )
          )
      );
    }
    return ResponseEntity.noContent().build();

  }



  @PostMapping("/offers")
  ResponseEntity<Void> createOffer(@RequestBody @Valid OfferRequest offerRequest,Principal principal) {
    MyUser user = userRepository.findMyUserByUsername(principal.getName()).get();

    OfferDTO offer = offerService.createOffer(user.getProfile().getId(), offerRequest);

    return ResponseEntity.created(ucb.path("/profile/offers/{id}")
            .buildAndExpand(Map.of("id",offer.id()))
        .toUri()).build();

  }

  @GetMapping("/offers")
  ResponseEntity<List<OfferDTO>> getOffers(Principal principal) {
    MyUser user = userRepository.findMyUserByUsername(principal.getName()).get();
    var offers = offerService.findOffersByProfileId(user.getProfile().getId());
    if (offers.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(offers);

  }

  @GetMapping("/offers/{offer_id}")
  ResponseEntity<OfferDTO> getOffer(@PathVariable("offer_id") Long offerId, Principal principal) {

    return ResponseEntity.ok(offerService.findOfferByIdAndUser(offerId, principal));

  }

  @DeleteMapping("/offers/{offer_id}")
  ResponseEntity<Void> deleteOffer(@PathVariable("offer_id") Long offerId, Principal principal) {

    offerService.deleteOfferByIdAndUser(offerId, principal);
    return ResponseEntity.noContent().build();

  }
  //TODO zabezpiecz endpointy ofert tylko dla sprzedajacych
  @PutMapping("/offers/{offer_id}")
  ResponseEntity<Void> updateOffer(@PathVariable("offer_id") Long offerId,
                                   Principal principal,
                                   @RequestBody @Valid OfferRequest offerRequest) {
    offerService.updateOffer(offerId,offerRequest,principal);
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler(ProfileAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)

  public ErrorResponse handleProfileAlreadyExistsException(ProfileAlreadyExistsException ex) {
    return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
  }

  @ExceptionHandler({ProfileNotFoundException.class, OfferNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleProfileNotFoundException(Throwable ex) {
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
