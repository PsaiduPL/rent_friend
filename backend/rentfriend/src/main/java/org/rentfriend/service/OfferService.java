package org.rentfriend.service;


import org.rentfriend.entity.Offer;
import org.rentfriend.entity.OfferDTO;
import org.rentfriend.entity.Profile;
import org.rentfriend.repository.OfferRepository;
import org.rentfriend.repository.ProfileRepository;
import org.rentfriend.requestData.OfferRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OfferService {

  final OfferRepository offerRepository;
  final ProfileRepository profileRepository;


  public OfferService(OfferRepository offerRepository, ProfileRepository profileRepository) {
    this.offerRepository = offerRepository;
    this.profileRepository = profileRepository;
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

  OfferDTO mapOffer(Offer offer){
    return new OfferDTO(offer.getId(),
        offer.getTitle(),
        offer.getDescription(),
        offer.getPricePerHour().doubleValue());
  }


}

