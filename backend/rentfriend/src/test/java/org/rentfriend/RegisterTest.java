package org.rentfriend;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.rentfriend.requestData.MyUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@ActiveProfiles("docker_test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RegisterTest {



  @LocalServerPort
  private int port;
  // Ta instancja będzie naszym "uwierzytelnionym" klientem
  private WebTestClient webTestClient;
  @Autowired
  private WebClient.Builder builder;
  static MultiValueMap<String, ResponseCookie> cookies;
  // Nadal potrzebujemy konfiguracji z użytkownikiem 'user'/'user' w pamięci,
  // np. z pliku TestSecurityConfig.java, aby logowanie mogło się powieść.
  String setUrl;

  @BeforeEach
  void setup() {
    // 1. Wykonaj programistyczne logowanie, aby uzyskać ciasteczko sesyjne
    String baseUrl = "http://localhost:" + port;

    WebTestClient loginClient = WebTestClient.bindToServer()
        .baseUrl(baseUrl)
        .build();


  }
    @ParameterizedTest
    @ValueSource(strings = {"SELLER","BUYER"})
    void shouldReturnOkAfterRegister(String role){
        MyUserRequest user = new MyUserRequest("user"+role,"user","user@gmail.com");
        webTestClient.post().uri("/signup/"+role)
          .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(user)
          .exchange()
            .expectStatus().isOk();
    }
}
