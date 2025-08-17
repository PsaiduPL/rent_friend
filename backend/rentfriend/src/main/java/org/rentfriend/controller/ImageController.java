package org.rentfriend.controller;


import org.rentfriend.exception.ImageException;
import org.rentfriend.exception.ProfileNotFoundException;
import org.rentfriend.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
@RequestMapping("/img")
@RestController

public class ImageController {
  Logger logger = LoggerFactory.getLogger(ImageController.class);
  private final ImageService imageService;

  @Autowired
  public ImageController(ImageService imageService) {
    this.imageService = imageService;
  }

//  @GetMapping()
//  public String listUploadedFiles(Model model) throws IOException {
//    logger.info("listUploadedFiles_Start");
//    model.addAttribute("files", imageService.loadAll().map(
//            path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
//                "serveFile", path.getFileName().toString()).build().toUri().toString())
//        .collect(Collectors.toList()));
//    logger.info("listUploadedFiles_End");
//    return "form";
//  }

  @GetMapping("/{uuid}")
  @ResponseBody
  public ResponseEntity<Resource> serveFile(@PathVariable("uuid") String filename) {

    var file = imageService.loadAsResource(filename);

    if (file == null)
      return ResponseEntity.notFound().build();

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + file.getFilename() + "\"")
        .header("Content-Type", MediaType.APPLICATION_OCTET_STREAM.toString()).body(file);
  }

  @PostMapping()
  public ResponseEntity<Void> handleFileUpload(@RequestPart("file") MultipartFile file,
                                               Principal principal) {

    logger.info("uploadFile_Start");

    var uuid = imageService.store(file,principal);
//    redirectAttributes.addFlashAttribute("message",
//        "You successfully uploaded " + file.getOriginalFilename() + "!");
    logger.info("uploadFile_End");
    return ResponseEntity.created(UriComponentsBuilder.fromUriString("/img/{uuid}").buildAndExpand(uuid).toUri()).build();
  }
  @DeleteMapping("/{uuid}")
  public ResponseEntity<Void> deleteFile(@PathVariable("uuid") String filename,Principal principal) {
    imageService.deleteResource(filename,principal);

    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler(ImageException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleProfileNotFoundException(ImageException ex) {
    return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
  }
  public record ErrorResponse(int status, String message) {
  }
  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleProfileNotFoundException(ProfileNotFoundException ex) {
    return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Profile not found");
  }

}