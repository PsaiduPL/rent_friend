package org.rentfriend;


import org.apache.catalina.webresources.FileResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rentfriend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@ActiveProfiles({"docker", "init-data"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient

public class LoadBalancerTest {
    @Autowired
    UserRepository userRepository;
    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;
    @Autowired
    private WebClient.Builder builder;

    @Test
    void shouldUploadImageAndReturnCreatedStatus() throws IOException { // Bardziej opisowa nazwa testu

        String baseUrl = "http://localhost:" + port;

        webTestClient = WebTestClient.bindToServer()
            .baseUrl(baseUrl)
            .build();

        List<String> usernames = userRepository.getUsernames();

        // Ładuj plik z classpath, a nie z absolutnej ścieżki
        ClassPathResource fileResource = new ClassPathResource("straw.jpg");
        Assertions.assertTrue(fileResource.exists());
        Assertions.assertTrue(fileResource.contentLength()>0);
        for (var user : usernames) {
            if(user.equals("user")){
                break;
            }
            webTestClient.post()
                .uri("/img")
                .headers(h -> h.setBasicAuth(user, "123"))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", fileResource)) // <-- KLUCZOWA ZMIANA
                .exchange()
                .expectStatus().isCreated();
        }
    }
}



