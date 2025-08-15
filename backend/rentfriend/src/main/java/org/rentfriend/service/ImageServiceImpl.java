package org.rentfriend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.util.ThumbnailatorUtils;
import org.rentfriend.entity.ImageDB;
import org.rentfriend.entity.Profile;
import org.rentfriend.exception.ImageException;
import org.rentfriend.repository.ImageRepository;
import org.rentfriend.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
@Log4j2
@Service
public class ImageServiceImpl implements ImageService {
  final ObjectMapper objectMapper;
  final Path baseLocation;
  final ImageRepository imageRepository;
  final ProfileRepository profileRepository;
  AtomicInteger atomicInteger = new AtomicInteger(0);
  @Value("${image.storage.buckets}")
  Integer maxBuckets;
  @Autowired
  public ImageServiceImpl(String location, ImageRepository imageRepository,
                          ProfileRepository profileRepository,
                          ObjectMapper objectMapper) {
    this.baseLocation = Path.of(location);
    this.imageRepository = imageRepository;
    this.profileRepository = profileRepository;
    this.objectMapper = objectMapper;
  }

  @PostConstruct
  @Override
  public void init() {
    try {
      Files.createDirectories(baseLocation);
      for(int i = 0 ; i < maxBuckets ;i++){
        Files.createDirectories(baseLocation.resolve(Integer.toString(i)));
      }

    } catch (IOException e) {
      throw new ImageException("Could not initialize storage", e);
    }
    loadState();

  }
  @PreDestroy
  public void preDestroy() {
    saveState();
  }
  void loadState(){
    String config = "config.json";
    if(Path.of(config).toFile().exists()){
      try{

      Map<String,Object> obj = objectMapper.readValue(Path.of(config).toFile(), new TypeReference<Map<String,Object>>(){});
      atomicInteger.set((Integer)obj.get("last_index"));
        log.info("state of balancer loaded");
      }catch(Exception e){
        log.warn("failed to load balancer state IOError");
      }


    }else{
      log.warn("failed to load balancer state file doesnt exists");
    }

  }
  void saveState(){
    String config = "config.json";
    try{
    objectMapper.writeValue(Path.of(config).toFile(),Map.of("name","image_load_balancer","last_index",atomicInteger.get()));
      log.info("state of balancer saved");
    }catch(IOException e){
      log.warn("failed to save load balancer state");
    }
  }


  Integer roundRobin(){

    return atomicInteger.getAndIncrement() % maxBuckets;
  }
  @Transactional
  @Override
  public UUID store(MultipartFile file, Principal principal) {
    try {
      if (file.isEmpty()) {
        throw new ImageException("Failed to store empty file.");
      }
      UUID uuid = UUID.randomUUID();

      Path destinationFile = this.baseLocation.resolve(roundRobin().toString()).resolve(
              Path.of(uuid.toString() + "." + "JPEG"))
          .normalize();
      var profile = profileRepository.findProfileByUser_Username(principal.getName());
      if (profile.get().getProfileImage() != null) {
        throw new ImageException("File already exists.");
      }
      imageRepository.save(new ImageDB(uuid, destinationFile.toString(), profile.get()));

      System.out.println(destinationFile.toString());
//      if (!destinationFile.getParent().equals(this.baseLocation.toAbsolutePath())) {
//        // This is a security check
//        throw new ImageException(
//            "Cannot store file outside current directory.");
//      }
      System.out.println("formatss------" + ThumbnailatorUtils.getSupportedOutputFormats());
      try (InputStream inputStream = file.getInputStream()) {
        Thumbnails.of(inputStream)
            .allowOverwrite(true)
            .scale(0.7)
            .outputQuality(0.3)
            .outputFormat("JPEG")
            .toFile(destinationFile.toAbsolutePath().toFile());
//        Files.copy(inputStream, destinationFile,
//
//            StandardCopyOption.REPLACE_EXISTING);
      }
      return uuid;
    } catch (IOException e) {
      throw new ImageException("Failed to store file." + e.getMessage());
    }
  }

  @Override
  public Stream<Path> loadAll() {
    return Stream.empty();
  }

  @Override
  public Path load(String filename) {
    return baseLocation.resolve(filename);

  }

  @Override
  public Resource loadAsResource(String filename) {
    try {
      var img = imageRepository.findById(UUID.fromString(filename));
      if (img.isEmpty()) {
        throw new ImageException("Image not found for: " + filename);
      }
      Path file = Path.of(img.get().getUrl());
      Resource resource = new UrlResource(file.toUri());

      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new ImageException(
            "Could not read file: " + filename);

      }
    } catch (MalformedURLException e) {
      throw new ImageException("Could not read file: " + filename, e);
    }
  }

  @Override
  public void deleteAll() {

  }
  @Transactional
  public void deleteResource(String filename, Principal principal) {
    var img = imageRepository.findById(UUID.fromString(filename));
    if (img.isEmpty()) {
      throw new ImageException("Image not found for: " + filename);
    }
    Optional<Profile> profileO = profileRepository.findProfileByUser_Username(principal.getName());
    if (profileO.isPresent()) {
      var profile = profileO.get();
      if (profile.getProfileImage() != null) {
        if (profile.getProfileImage().getId().equals(UUID.fromString(filename))) {
          System.out.println("usuwam zdjecie");
          imageRepository.deleteImg(UUID.fromString(filename));
          return;
        }
      }
    }
    throw new ImageException("Image not found ");

  }
}
