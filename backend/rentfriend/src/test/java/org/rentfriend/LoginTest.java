package org.rentfriend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rentfriend.requestData.MyUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@ActiveProfiles("docker")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LoginTest {


  @LocalServerPort
  private int port;

  private WebTestClient webTestClient;
  @Autowired
  private WebClient.Builder builder;
  static MultiValueMap<String, ResponseCookie> cookies;

  String setUrl;

  @BeforeEach
  void setup() {

    String baseUrl = "http://localhost:" + port;

    webTestClient = WebTestClient.bindToServer()
        .baseUrl(baseUrl)
        .build();
    MyUserRequest user = new MyUserRequest("user","user","user@gmail.com");
    webTestClient.post().uri("/signup/"+"SELLER")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(user)
        .exchange()
        .expectStatus().isOk();

  }
  @Test
  void shouldReturnAcceptedAfterLogin(){
    webTestClient.post().uri("/login")

        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(MultiValueMap.fromSingleValue(Map.of("username", "user",
            "password", "user"))))
        .exchange().expectStatus().isAccepted();
  }
  @Test
  void shouldReturnNotFoundAfterLogin(){
    webTestClient.post().uri("/login")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(MultiValueMap.fromSingleValue(Map.of("username","userxD",
            "password", "user2")))).exchange().expectStatus().isNotFound();
  }
}



