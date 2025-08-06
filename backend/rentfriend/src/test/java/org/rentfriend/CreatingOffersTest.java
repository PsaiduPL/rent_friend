package org.rentfriend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rentfriend.requestData.MyUserRequest;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
@ActiveProfiles("docker")
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
  }
}
