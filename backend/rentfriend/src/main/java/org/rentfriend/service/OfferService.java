package org.rentfriend.service;


import org.rentfriend.entity.Offer;
import org.rentfriend.dto.OfferDTO;
import org.rentfriend.entity.Profile;
import org.rentfriend.exception.OfferNotFoundException;
import org.rentfriend.exception.ProfileNotFoundException;
import org.rentfriend.repository.OfferRepository;
import org.rentfriend.repository.ProfileRepository;
import org.rentfriend.repository.UserRepository;
import org.rentfriend.requestData.OfferRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class OfferService {

  final OfferRepository offerRepository;
  final ProfileRepository profileRepository;
  private final UserRepository userRepository;


  public OfferService(OfferRepository offerRepository, ProfileRepository profileRepository, UserRepository userRepository) {
    this.offerRepository = offerRepository;
    this.profileRepository = profileRepository;
    this.userRepository = userRepository;
  }

  public OfferDTO createOffer(Long profileId, OfferRequest offer){
    Offer offerDB = new Offer();
    Profile profileDB = new Profile();
    profileDB.setId(profileId);
    offerDB.setTitle(offer.title());
    offerDB.setDescription(offer.description());
    offerDB.setPricePerHour(BigDecimal.valueOf(offer.pricePerHour()));
    offerDB.setProfile(profileDB);

    return mapOffer(offerRepository.save(offerDB));

  }
  public List<OfferDTO> findOffersByProfileId(Long profileId){

      Optional<Profile> profileDB = profileRepository.findById(profileId);
      if(profileDB.isPresent()){
        var profile = profileDB.get();
        return profile.getOfferList().stream().map(o->mapOffer(o)).toList();
      }
      throw new ProfileNotFoundException("Profile not found");
    }
    public OfferDTO findOfferByIdAndUser(Long offerId, Principal principal){
    var usr = userRepository.findTopMyUserByUsername(principal.getName());
    Optional<Offer> offerDB = offerRepository.findByIdAndProfile_Id(offerId,usr.getProfile().getId());
    if(offerDB.isPresent()){
      var offer = offerDB.get();
      return mapOffer(offer);
    }
    throw new OfferNotFoundException("Offer doesnt exists");
    }

  OfferDTO mapOffer(Offer offer){
    return new OfferDTO(offer.getId(),
        offer.getTitle(),
        offer.getDescription(),
        offer.getPricePerHour().doubleValue());
  }


}

