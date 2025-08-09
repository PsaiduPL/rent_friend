package org.rentfriend.service;


import org.rentfriend.dto.BodyParameterDTO;
import org.rentfriend.dto.InterestDTO;
import org.rentfriend.dto.ProfileDTO;
import org.rentfriend.entity.*;
import org.rentfriend.exception.ProfileAlreadyExistsException;
import org.rentfriend.exception.ProfileNotFoundException;
import org.rentfriend.repository.ProfileRepository;
import org.rentfriend.repository.UserRepository;
import org.rentfriend.requestData.BodyParameterRequest;
import org.rentfriend.requestData.OfferRequest;
import org.rentfriend.requestData.ProfileRequest;
import org.rentfriend.requestData.ProfileUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfileService {

  final UserRepository userRepository;
  final ProfileRepository profileRepository;
  final UriComponentsBuilder ucb;

  public ProfileService(UserRepository userRepository, ProfileRepository profileRepository,
                        UriComponentsBuilder ucb) {
    this.userRepository = userRepository;
    this.profileRepository = profileRepository;
    this.ucb = ucb;
  }
//  }
//  @Transactional
//  public List<ProfileDTO> getAllSellerProfiles(Pageable pageable) {
//    Page<ProfileDTO> profiles =  profileRepository.findProfilesByUser_roleAndUser_Profile_Id_IsNotNull("SELLER",PageRequest.of(
//        pageable.getPageNumber(),
//        pageable.getPageSize(),
//        pageable.getSort())).map(profile->mapProfile(profile));
//    return profiles.getContent();
//  }


  @Transactional
  public ProfileDTO createProfile(ProfileRequest profileRequest
      , Principal principal) {

    MyUser myUser = userRepository.findTopMyUserByUsername(principal.getName());
    if (profileRepository.findProfileByUser_Id(myUser.getId()).isPresent()) {
      throw new ProfileAlreadyExistsException("Profile already exists");
    }
    Profile profile = new Profile();
    profile.setUser(myUser);
    profile.setName(profileRequest.name());
    profile.setDescription(profileRequest.description());
    profile.setGender(profileRequest.gender());
    profile.setInterestList(profileRequest.interestList().stream().map(a->
        new Interest(a.id(),null,null)).toList());
    Optional<BodyParameterRequest> bodyParameterRequest = Optional.ofNullable(profileRequest.bodyParameter());

    bodyParameterRequest.ifPresent(bodyParameterRequest1 -> profile.setBodyParameter(new BodyParameter(null, bodyParameterRequest1.height(),
        bodyParameterRequest1.weight(), profile)));

    profile.setCity(profileRequest.city());
    profile.setAge(profileRequest.age());
    Profile created = profileRepository.save(profile);


    return mapProfile(created);
  }
  @Transactional
  public void updateProfile(ProfileRequest profileRequest, Principal principal) {
    MyUser myUser = userRepository.findTopMyUserByUsername(principal.getName());
    Optional<Profile> profileDB = profileRepository.findProfileByUser_Id(myUser.getId());
    if (profileDB.isPresent()) {



          var profile = profileDB.get();

          profile.setName(profileRequest.name());
          profile.setDescription(profileRequest.description());
          profile.setGender(profileRequest.gender());
          profile.setOfferList(profile.getOfferList());
          profile.setCity(profileRequest.city());
          profile.setAge(profileRequest.age());
          BodyParameter bodyParameter = profile.getBodyParameter();
          bodyParameter.setHeight(profileRequest.bodyParameter().height());
          bodyParameter.setWeight(profileRequest.bodyParameter().weight());
          profile.setBodyParameter(bodyParameter);

          profile.setInterestList(profileRequest.interestList().stream().map(a->
              new Interest(a.id(),null,null)).collect(Collectors.toList()));
          profileRepository.save(profile);
          return ;
        }
        throw new ProfileNotFoundException("Profile not found");
  }

  public ProfileDTO findProfileById(Long id) {
    Optional<Profile> profile = profileRepository.findById(id);

    if (profile.isPresent()) {
      return mapProfile(profile.get());
    }
    throw new ProfileNotFoundException("Profile not found");

  }



  public ProfileDTO mapProfile(Profile profile) {
    BodyParameterDTO body = null;
    if (profile.getBodyParameter() != null) {
      body = new BodyParameterDTO(profile.getBodyParameter().getHeight(),
          profile.getBodyParameter().getWeight());
    }
//    }else{
//      body = new BodyParameterDTO(180.0,80.0);
//    }
    ProfileDTO profileDTO = new ProfileDTO(
        profile.getId(),
        profile.getUser().getRole(),
        profile.getName(),
        profile.getAge(),
        profile.getCity(),
        profile.getDescription(),
        profile.getGender(),
        body,
        profile.getInterestList().stream().map(i->{
          return new InterestDTO(i.getId(), i.getInterest());
        }).toList()

    );
    return profileDTO;
  }
}
