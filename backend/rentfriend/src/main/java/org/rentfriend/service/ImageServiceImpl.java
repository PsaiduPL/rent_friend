package org.rentfriend.service;

import jakarta.annotation.PostConstruct;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.util.ThumbnailatorUtils;
import org.rentfriend.entity.ImageDB;
import org.rentfriend.entity.Profile;
import org.rentfriend.exception.ImageException;
import org.rentfriend.repository.ImageRepository;
import org.rentfriend.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class ImageServiceImpl implements ImageService {

  final Path baseLocation;
  final ImageRepository imageRepository;
  final ProfileRepository profileRepository;

  @Autowired
  public ImageServiceImpl(String location, ImageRepository imageRepository,
                          ProfileRepository profileRepository) {
    this.baseLocation = Path.of(location);
    this.imageRepository = imageRepository;
    this.profileRepository = profileRepository;
  }

  @PostConstruct
  @Override
  public void init() {
    try {
      Files.createDirectories(baseLocation);
    } catch (IOException e) {
      throw new ImageException("Could not initialize storage", e);
    }
  }

  @Transactional
  @Override
  public UUID store(MultipartFile file, Principal principal) {
    try {
      if (file.isEmpty()) {
        throw new ImageException("Failed to store empty file.");
      }
      UUID uuid = UUID.randomUUID();

      Path destinationFile = this.baseLocation.resolve(
              Path.of(uuid.toString() + "." + "JPEG"))
          .normalize().toAbsolutePath();
      var profile = profileRepository.findProfileByUser_Username(principal.getName());
      if (profile.get().getProfileImage() != null) {
        throw new ImageException("File already exists.");
      }
      imageRepository.save(new ImageDB(uuid, destinationFile.toString(), profile.get()));

      System.out.println(destinationFile.toString());
      if (!destinationFile.getParent().equals(this.baseLocation.toAbsolutePath())) {
        // This is a security check
        throw new ImageException(
            "Cannot store file outside current directory.");
      }
      System.out.println("formatss------" + ThumbnailatorUtils.getSupportedOutputFormats());
      try (InputStream inputStream = file.getInputStream()) {
        Thumbnails.of(inputStream)
            .allowOverwrite(true)
            .scale(0.7)
            .outputQuality(0.3)
            .outputFormat("JPEG")
            .toFile(destinationFile.toFile());
//        Files.copy(inputStream, destinationFile,
//
//            StandardCopyOption.REPLACE_EXISTING);
      }
      return uuid;
    } catch (IOException e) {
      throw new ImageException("Failed to store file.", e);
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

          imageRepository.delete(img.get());
        }
      }
    }
    throw new ImageException("Image not found ");

  }
}
