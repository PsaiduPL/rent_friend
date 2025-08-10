package org.rentfriend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.rentfriend.dto.OfferDTO;
import org.rentfriend.requestData.OfferRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.net.URI;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
@ActiveProfiles({"docker","init-user"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CreatingOffersTest {


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

    WebTestClient.ResponseSpec r = loginClient.post().uri("/login")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(MultiValueMap.fromSingleValue(Map.of("username", "john",
            "password", "john"))))
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
  void shouldCreateNewOffer(){

    OfferRequest offerRequest = new OfferRequest("przejade sie rowerem",
        "czesc przejechalbym sie rowerem wzludz wisly",20.0);

    URI uri  = webTestClient.post().uri("/profile/offers")
        .bodyValue(offerRequest).exchange().expectStatus().isCreated().returnResult(Object.class).getResponseHeaders().getLocation();
    System.out.println("uri ---------" + uri);
    webTestClient.get().uri(uri.toString()).exchange().expectStatus().isOk().expectBody(OfferDTO.class).value(
        offer->{
        assertThat(offer.title()).isEqualTo(offerRequest.title());
        assertThat(offer.description()).isEqualTo(offerRequest.description());
        assertThat(offer.pricePerHour()).isEqualTo(offerRequest.pricePerHour());


        }
    );
  }

  @Test
  void shouldCreateNewOfferAndDelete(){
    OfferRequest offerRequest = new OfferRequest("przejade sie rowerem",
        "czesc przejechalbym sie rowerem wzludz wisly",20.0);

    URI location  = webTestClient.post().uri("/profile/offers")
        .bodyValue(offerRequest).exchange().expectStatus().isCreated().returnResult(Object.class).getResponseHeaders().getLocation();
    System.out.println("uri ---------" + location);

    webTestClient.delete().uri(location.toString()).exchange().expectStatus().isNoContent();

    webTestClient.get().uri(location.toString()).exchange().expectStatus().isNotFound();
  }
  @Test
  void shouldReturnNotFoundAfterDeletinNotExistingOffer(){

    webTestClient.delete().uri("/profile/offers/999").exchange().expectStatus().isNotFound();

  }
  @Test
  void shouldCreateOfferAndUpdateIt(){
    OfferRequest offerRequest = new OfferRequest("przejade sie rowerem",
        "czesc przejechalbym sie rowerem wzludz wisly",20.0);

    URI location  = webTestClient.post().uri("/profile/offers")
        .bodyValue(offerRequest).exchange().expectStatus().isCreated().returnResult(Object.class).getResponseHeaders().getLocation();
    System.out.println("uri ---------" + location);

    OfferRequest offerRequest1 = new OfferRequest("przejade sie szybko rowerem",offerRequest.description(),
        offerRequest.pricePerHour());
    webTestClient.put().uri(location.toString()).bodyValue(offerRequest1).exchange().expectStatus().isNoContent();

    webTestClient.get().uri(location.toString()).exchange().expectStatus().isOk().expectBody(OfferDTO.class).value(
        offer->{
          assertThat(offer.title()).isEqualTo(offerRequest1.title());
          assertThat(offer.description()).isEqualTo(offerRequest1.description());
          assertThat(offer.pricePerHour()).isEqualTo(offerRequest1.pricePerHour());


        }
    );
  }
  @Test
  void shouldReturnNotFoundAfterUpdateItBadId(){

    OfferRequest offerRequest1 = new OfferRequest("przejade sie rowerem",
        "czesc przejechalbym sie rowerem wzludz wisly",20.0);
    webTestClient.put().uri("/profile/offers/9999").bodyValue(offerRequest1).exchange().expectStatus().isNotFound();


  }
  @ParameterizedTest
  @MethodSource("provideInvalidOfferRequests")
  void shouldReturnBadRequestWhenCreatingOfferWithInvalidData(OfferRequest invalidRequest, String expectedErrorFragment) {
    webTestClient.post().uri("/profile/offers")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(invalidRequest)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody(Map.class).value(response -> {
          // Wypisanie odpowiedzi jest pomocne przy debugowaniu
          System.out.println("Validation response for input " + invalidRequest + ": " + response);
          String responseBodyAsString = response.toString();
          assertThat(responseBodyAsString).contains(expectedErrorFragment);
        });
  }
  private static Stream<Arguments> provideInvalidOfferRequests() {
    // Poprawne dane, które będziemy modyfikować w każdym przypadku testowym
    String validTitle = "Poprawny tytuł";
    String validDescription = "To jest wystarczająco długi i poprawny opis oferty.";
    Double validPrice = 150.0;

    return Stream.of(
        // --- Walidacja pola 'title' ---
        Arguments.of(new OfferRequest(null, validDescription, validPrice), "must not be blank"),
        Arguments.of(new OfferRequest("", validDescription, validPrice), "must not be blank"),
        Arguments.of(new OfferRequest("   ", validDescription, validPrice), "must not be blank"),
        Arguments.of(new OfferRequest("a".repeat(251), validDescription, validPrice), "size"),

        // --- Walidacja pola 'description' ---
        Arguments.of(new OfferRequest(validTitle, null, validPrice), "null"),
        Arguments.of(new OfferRequest(validTitle, "", validPrice), "blank"),
        Arguments.of(new OfferRequest(validTitle, "   ", validPrice), "blank"),
        Arguments.of(new OfferRequest(validTitle, "za krótki", validPrice), "size"), // description.length() < 10
        Arguments.of(new OfferRequest(validTitle, "a".repeat(2501), validPrice), "size"),

        // --- Walidacja pola 'pricePerHour' ---
        Arguments.of(new OfferRequest(validTitle, validDescription, 0.0), "must be greater than or equal to "),      // price < 1
        Arguments.of(new OfferRequest(validTitle, validDescription, 100000.0), "must be less than or equal to 99999") // price > 99999
    );
  }

}
