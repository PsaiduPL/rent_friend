package org.rentfriend.service;


import org.rentfriend.dto.BodyParameterDTO;
import org.rentfriend.dto.ProfileDTO;
import org.rentfriend.entity.BodyParameter;
import org.rentfriend.entity.MyUser;
import org.rentfriend.entity.Profile;
import org.rentfriend.exception.ProfileAlreadyExistsException;
import org.rentfriend.exception.ProfileNotFoundException;
import org.rentfriend.repository.ProfileRepository;
import org.rentfriend.repository.UserRepository;
import org.rentfriend.requestData.ProfileRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

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
  public ProfileDTO createProfile(ProfileRequest profileRequest
      , Principal principal){

    MyUser myUser = userRepository.findTopMyUserByUsername(principal.getName());
    if(profileRepository.findProfileByUser_Id(myUser.getId()).isPresent()){
      throw new ProfileAlreadyExistsException("Profile already exists");
    }
    Profile profile = new Profile();
    profile.setUser(myUser);
    profile.setName(profileRequest.name());
    profile.setDescription(profileRequest.description());
    profile.setBodyParameter(new BodyParameter(null,profileRequest.bodyParameter().height(),
        profileRequest.bodyParameter().weight(),profile));
    profile.setCity(profileRequest.city());
    profile.setAge(profileRequest.age());
    Profile created = profileRepository.save(profile);




    return mapProfile(created);
  }
  public ProfileDTO findProfileById(Long id){
    Optional<Profile> profile =  profileRepository.findById(id);

    if(profile.isPresent()){
      return mapProfile(profile.get());
    }
    throw new ProfileNotFoundException("Profile not found");

  }
  ProfileDTO mapProfile(Profile profile){
    ProfileDTO profileDTO = new ProfileDTO(
        profile.getId(),
        profile.getUser().getRole(),
        profile.getName(),
        profile.getAge(),
        profile.getCity(),
        profile.getDescription(),
        new BodyParameterDTO(profile.getBodyParameter().getHeight(),
            profile.getBodyParameter().getWeight())
    );
    return profileDTO;
  }
}
