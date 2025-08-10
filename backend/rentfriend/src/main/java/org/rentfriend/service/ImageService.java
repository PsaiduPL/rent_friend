package org.rentfriend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.security.Principal;
import java.util.UUID;
import java.util.stream.Stream;

public interface ImageService {


    void init();

    UUID store(MultipartFile file, Principal principal);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

    void deleteResource(String filename,Principal principal);

}
