package org.rentfriend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rentfriend.dto.OfferDTO;
import org.rentfriend.entity.Profile;
import org.rentfriend.requestData.BodyParameterRequest;
import org.rentfriend.requestData.InterestRequest;
import org.rentfriend.requestData.OfferRequest;
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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"docker","init-user"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EndpointsRoleAuthorizationBUYER {

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
        .body(BodyInserters.fromFormData(MultiValueMap.fromSingleValue(Map.of("username", "pawel",
            "password", "pawel"))))
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
  void shouldForbiden_Profile_Offers(){
    /// GET
    webTestClient.get().uri("/profile/offers").exchange().expectStatus().isForbidden();

    OfferRequest offerRequest = new OfferRequest("przejade sie rowerem",
        "czesc przejechalbym sie rowerem wzludz wisly",20.0);
    ///  POST
     webTestClient.post().uri("/profile/offers")
        .bodyValue(offerRequest).exchange().expectStatus().isForbidden();


  }
  @Test
  void shouldForbiden_profile_offers_id(){
    webTestClient.get().uri("/profile/offers/1").exchange().expectStatus().isForbidden();

    webTestClient.delete().uri("/profile/offers/1").exchange().expectStatus().isForbidden();

    webTestClient.put().uri("/profile/offers/1").bodyValue(new OfferRequest(null,null,null)).exchange().expectStatus().isForbidden();




  }
}
