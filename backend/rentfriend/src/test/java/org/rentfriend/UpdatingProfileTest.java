package org.rentfriend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rentfriend.dto.ProfileDTO;
import org.rentfriend.dto.ProfileDetailsDTO;
import org.rentfriend.requestData.BodyParameterRequest;
import org.rentfriend.requestData.InterestRequest;
import org.rentfriend.requestData.MyUserRequest;
import org.rentfriend.requestData.ProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
@ActiveProfiles("docker")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UpdatingProfileTest {


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
  void shouldReturnNotFoundUpdateProfileWithoutCreating(){

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

    webTestClient.put().uri("/profile")
        .bodyValue(profileRequest).exchange().expectStatus().isNotFound();

  }
  @Test
  void shouldUpdateProfile(){
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

    webTestClient.post().uri("/profile")
        .bodyValue(profileRequest).exchange().expectStatus().isCreated();

    EntityExchangeResult<ProfileDetailsDTO> profileEx = webTestClient.get().uri("/profile").exchange().expectStatus().isOk().expectBody(ProfileDetailsDTO.class).value(
        b->{
          var c = b.profile();
          assertThat(c.interestList()).isNotNull();
        }
    ).returnResult();
    ProfileDTO profileDTO =  profileEx.getResponseBody().profile();
    ProfileRequest dto = new ProfileRequest(
        "Big mike",
        profileDTO.description(),
        profileDTO.city(),
        30,
        profileDTO.gender(),
        profileDTO.interestList().stream().map(a->new InterestRequest(a.id())).toList(),
        new BodyParameterRequest(profileDTO.bodyParameter().height(),profileDTO.bodyParameter().weight()));

    webTestClient.put().uri("/profile").bodyValue(dto).exchange().expectStatus().isNoContent();

    webTestClient.get().uri("/profile").exchange().expectStatus().isOk().expectBody(ProfileDetailsDTO.class).value(
        profileDetailsDTO -> {
          var profile = profileDetailsDTO.profile();
          assertThat(profile.name()).isEqualTo("Big mike");
          assertThat(profile.role()).isEqualTo("SELLER");
          assertThat(profile.age()).isEqualTo(30);
        }
    );
  }
  }

