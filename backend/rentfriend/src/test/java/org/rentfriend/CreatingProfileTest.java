package org.rentfriend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.List;
import java.util.Map;
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
    System.out.println("LOCATION "+location);;
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

            }
        );
  }//TODO ogarnij wiecej testow na tworzenie profilu i sprawdz czy cala logika ma sens
}
