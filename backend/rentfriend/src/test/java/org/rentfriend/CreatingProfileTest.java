package org.rentfriend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.rentfriend.dto.ProfileDTO;
import org.rentfriend.entity.BodyParameter;
import org.rentfriend.entity.Interest;
import org.rentfriend.entity.Profile;
import org.rentfriend.requestData.BodyParameterRequest;
import org.rentfriend.requestData.InterestRequest;
import org.rentfriend.requestData.MyUserRequest;
import org.rentfriend.requestData.ProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
@ActiveProfiles("docker")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CreatingProfileTest {


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
    System.out.println("creating login client ---------------");
    WebTestClient loginClient = WebTestClient.bindToServer()
        .baseUrl(baseUrl)
        .build();
    MyUserRequest user = new MyUserRequest("user","user","user@gmail.com");
    loginClient.post().uri("/signup/SELLER")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(user)
        .exchange()
        .expectStatus().isOk();
    System.out.println("registration succeedd-----------");
    WebTestClient.ResponseSpec r = loginClient.post().uri("/login")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(MultiValueMap.fromSingleValue(Map.of("username", "user",
            "password", "user"))))
        .exchange().expectStatus().isAccepted();
    FluxExchangeResult<Object> a = r.returnResult(Object.class);

    cookies = a.getResponseCookies();
//    cookies.entrySet().stream().forEach(b->System.out.println(b.getKey()+" "+
//        b.getValue().toString()));
//    //	System.out.println(loginCokie.getValue());
    webTestClient = WebTestClient.bindToServer().baseUrl(baseUrl).
        defaultCookie("JSESSIONID",cookies.asSingleValueMap().get("JSESSIONID").getValue())
        .build();

  }
  @Test
  void shouldReturnUrlAfterSuccesfulProfileCreationAndCreateProfileWithoutBodyParameter(){
    ProfileRequest profileRequest = new ProfileRequest("John Dick",
        """
            Young boy from your dreams
            """,
        "Kraków",
        21,
        "male",
        List.of(
            new InterestRequest(1L),
            new InterestRequest(2L)
        ),

    null);
    FluxExchangeResult<Profile> profile = webTestClient.post().uri("/profile")
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(profileRequest)
        .exchange()
        .expectStatus().isCreated().returnResult(new ParameterizedTypeReference<Profile>() {});
    URI location = profile.getResponseHeaders().getLocation();
    System.out.println("LOCATION "+location);;
    webTestClient.get().uri(location.toString())
        .exchange()
        .expectStatus().isOk().expectBody(Profile.class).value(
            p ->{
              assertThat(p.getAge()).isEqualTo(profileRequest.age());
              assertThat(p.getName()).isEqualTo(profileRequest.name());
              assertThat(p.getCity()).isEqualTo(profileRequest.city());
              assertThat(p.getDescription()).isEqualTo(profileRequest.description());
              assertThat(p.getBodyParameter()).isNull();

            }
        );
  }
  @Test
  void shouldReturnUrlAfterSuccesfulProfileCreationAndCreateProfileWithBodyParameter(){
    ProfileRequest profileRequest = new ProfileRequest("John Dick",
        """
            Young boy from your dreams
            """,
        "Kraków",
        21,
        "male",
        List.of(
            new InterestRequest(1L),
            new InterestRequest(2L)
        ),
        new BodyParameterRequest(176.5,75.4));
    FluxExchangeResult<Profile> profile = webTestClient.post().uri("/profile")
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(profileRequest)
        .exchange()
        .expectStatus().isCreated().returnResult(new ParameterizedTypeReference<Profile>() {});
    URI location = profile.getResponseHeaders().getLocation();
    System.out.println("LOCATION "+location);
    webTestClient.get().uri(location.toString())
        .exchange()
        .expectStatus().isOk().expectBody(ProfileDTO.class).value(
            p ->{
              assertThat(p.age()).isEqualTo(profileRequest.age());
              assertThat(p.name()).isEqualTo(profileRequest.name());
              assertThat(p.city()).isEqualTo(profileRequest.city());
              assertThat(p.description()).isEqualTo(profileRequest.description());
              assertThat(p.bodyParameter().height()).isEqualTo(profileRequest.bodyParameter().height());
              assertThat(p.bodyParameter().weight()).isEqualTo(profileRequest.bodyParameter().weight());
              assertThat(p.gender()).isEqualTo(profileRequest.gender());
            }
        );
  }
  @Test
  void shouldReturnNotFoundForNonExistentProfile() {
    webTestClient.get().uri("/profile/99999")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }
  @Test
  void shouldReturnBadRequestWhenCreatingProfileWithInvalidData() {
    // Tworzymy request z niepoprawnymi danymi
    ProfileRequest invalidProfileRequest = new ProfileRequest(
        "2", // Puste imię - nie powinno przejść walidacji
        "Valid description",
        "City",
        -25, // Ujemny wiek - nie powinno przejść walidacji
        "male",
        null,
        null
    );

    webTestClient.post().uri("/profile")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(invalidProfileRequest)
        .exchange()
        .expectStatus().isBadRequest().expectBody(Map.class).value(
            response -> {
              System.out.println(response.get("message").toString());

            }
        );
  }

  @Test
  void shouldReturnUnauthorizedWhenCreatingProfileWithoutAuthentication() {
    ProfileRequest profileRequest = new ProfileRequest("Anonymous", "Desc", "Gdańsk", 40, "female", null, null);
    WebTestClient unauthenticatedWebTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    unauthenticatedWebTestClient.post().uri("/profile")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(profileRequest)
        .exchange()
        .expectStatus().isUnauthorized();
  }
  @ParameterizedTest
  @MethodSource("provideInvalidProfileRequests")
  void shouldReturnBadRequestForVariousInvalidProfileData(ProfileRequest invalidRequest, String expectedErrorFragment) {
    webTestClient.post().uri("/profile")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(invalidRequest)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody(Map.class).value(response -> {
          // Wypisujemy całą odpowiedź, aby ułatwić debugowanie w razie problemów
          System.out.println("Validation response for input " + invalidRequest + ": " + response);
          String allMessages = response.toString();
          assertThat(allMessages).contains(expectedErrorFragment);
        });
  }

  /**
   * Dostawca danych dla sparametryzowanego testu walidacji.
   * Zwraca strumień argumentów, gdzie każdy argument to:
   * 1. Niepoprawny obiekt ProfileRequest
   * 2. Oczekiwany fragment komunikatu błędu
   */
  private static Stream<Arguments> provideInvalidProfileRequests() {
    // Poprawne dane, które będziemy "psuć" w każdym przypadku testowym
    String validName = "Jan Kowalski";
    String validDescription = "Ciekawy opis profilu.";
    String validCity = "Warszawa";
    Integer validAge = 25;
    String validGender = "male";
    List<InterestRequest> validInterests = List.of(new InterestRequest(1L));

    return Stream.of(
        // --- Walidacja pola 'name' ---
        Arguments.of(new ProfileRequest(null, validDescription, validCity, validAge, validGender, validInterests, null), "name null"),
        Arguments.of(new ProfileRequest("", validDescription, validCity, validAge, validGender, validInterests, null), "name cannot be blank"),
        Arguments.of(new ProfileRequest("  ", validDescription, validCity, validAge, validGender, validInterests, null), "name cannot be blank"),
        Arguments.of(new ProfileRequest("a".repeat(51), validDescription, validCity, validAge, validGender, validInterests, null), "name max 50 characters"),

        // --- Walidacja pola 'description' ---
        Arguments.of(new ProfileRequest(validName, null, validCity, validAge, validGender, validInterests, null), "description cannot be empty"),
        Arguments.of(new ProfileRequest(validName, "", validCity, validAge, validGender, validInterests, null), "description cannot be empty"),
        Arguments.of(new ProfileRequest(validName, "a".repeat(1501), validCity, validAge, validGender, validInterests, null), "description max 1500 characters"),

        // --- Walidacja pola 'city' ---
        Arguments.of(new ProfileRequest(validName, validDescription, null, validAge, validGender, validInterests, null), "city cannot be empty"),
        Arguments.of(new ProfileRequest(validName, validDescription, "", validAge, validGender, validInterests, null), "city cannot be empty"),
        Arguments.of(new ProfileRequest(validName, validDescription, "a".repeat(151), validAge, validGender, validInterests, null), "size must be between 0 and 150"),

        // --- Walidacja pola 'age' ---
        Arguments.of(new ProfileRequest(validName, validDescription, validCity, 17, validGender, validInterests, null), "must be greater than or equal to 18"), // Lub dokładny komunikat "must be greater than or equal to 18"
        Arguments.of(new ProfileRequest(validName, validDescription, validCity, 101, validGender, validInterests, null), "must be less than or equal to 100}"), // Lub dokładny komunikat "must be less than or equal to 100"

        // --- Walidacja pola 'gender' ---
        Arguments.of(new ProfileRequest(validName, validDescription, validCity, validAge, null, validInterests, null), "must not be null"),
        Arguments.of(new ProfileRequest(validName, validDescription, validCity, validAge, "", validInterests, null), "must not be blank"),

        // --- Walidacja pola 'interestList' ---
        Arguments.of(new ProfileRequest(validName, validDescription, validCity, validAge, validGender, null, null), "interests cannot be empty"),
        Arguments.of(new ProfileRequest(validName, validDescription, validCity, validAge, validGender, Collections.emptyList(), null), "must not be empty")
    );
  }
}

