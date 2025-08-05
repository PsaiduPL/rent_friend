package org.rentfriend;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.rentfriend.requestData.MyUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;

@ActiveProfiles("docker")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RegisterTest {



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
    @ParameterizedTest
    @ValueSource(strings = {"Seller","ADMIN","Buyer","asdsadsas"})
    void shouldReturnBadRequestAndFailRegisterBecauseOfBadRoles(String role){
      MyUserRequest user = new MyUserRequest("user"+role,"user","user@gmail.com");
      webTestClient.post().uri("/signup/"+role)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(user)
          .exchange()
          .expectStatus().isBadRequest()
          .expectBody(Map.class).value(
              response -> {
                String message = response.get("message").toString();
                assertThat(message).isEqualTo("Bad role entered");
                System.out.println(message);
              }
              );
    }
    @ParameterizedTest
    @ValueSource(strings = {"SELLER","BUYER"})
    void shouldReturnUserAlreadyExists(String role){
      MyUserRequest user = new MyUserRequest("user"+role,"user","user@gmail.com");
      webTestClient.post().uri("/signup/"+role)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(user)
          .exchange()
          .expectStatus().isOk();

      webTestClient.post().uri("/signup/"+role)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(user)
          .exchange()
          .expectStatus().isBadRequest()
          .expectBody(Map.class).value(
              response -> {
                String message = response.get("message").toString();
                assertThat(message).isEqualTo("User with this email/username already exists");
                System.out.println(message);
              }
          );


    }
    @Test
  void shouldReturnBadRequestEmptyUsername(){
    MyUserRequest user = new MyUserRequest("","user","user@gmail.com");
    webTestClient.post().uri("/signup/"+"SELLER")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(user)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody(Map.class).value(
            response -> {
              String message = response.get("message").toString();
              assertThat(message).isEqualTo("username cannot be blank");
              System.out.println(message);
            }
        );
    }
    @Test
    void shouldReturnBadRequestEmptyPassword(){
      MyUserRequest user = new MyUserRequest("user","","user@gmail.com");
      webTestClient.post().uri("/signup/"+"SELLER")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(user)
          .exchange()
          .expectStatus().isBadRequest()
          .expectBody(Map.class).value(
              response -> {
                String message = response.get("message").toString();
                assertThat(message).isEqualTo("password cannot be blank");
                System.out.println(message);
              }
          );
    }
    @Test
  void shouldReturnBadRequestInvalidEmail(){
      MyUserRequest user = new MyUserRequest("user","user","usergmail.com");
      webTestClient.post().uri("/signup/"+"SELLER")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(user)
          .exchange()
          .expectStatus().isBadRequest()
          .expectBody(Map.class).value(
              response -> {
                String message = response.get("message").toString();
                assertThat(message).isEqualTo("invalid email");
                System.out.println(message);
              }
          );

    }
    @Test
  void shouldReturnBadRequestBlankEmail(){
    MyUserRequest user = new MyUserRequest("user","user","");
    webTestClient.post().uri("/signup/"+"SELLER")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(user)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody(Map.class).value(
            response -> {
              String message = response.get("message").toString();
              assertThat(message).isEqualTo("email cannot be blank");
              System.out.println(message);
            }
        );

  }
  @Test
  void shouldReturnBadRequestToLongUsername(){
    String username = "a".repeat(100);
    MyUserRequest user = new MyUserRequest(username,"user","user@gmail.com");

    webTestClient.post().uri("/signup/"+"SELLER")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(user)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody(Map.class).value(
            response -> {
              String message = response.get("message").toString();
              assertThat(message).isEqualTo("username max 50 characters");
              System.out.println(message);
            }
        );

  }
  @Test
  void shouldReturnBadRequestToLongPassword(){
    String password = "a".repeat(50);
    MyUserRequest user = new MyUserRequest("user",password,"user@gmail.com");
    webTestClient.post().uri("/signup/"+"SELLER")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(user)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody(Map.class).value(
            response -> {
              String message = response.get("message").toString();
              assertThat(message).isEqualTo("password max 20 characters");
              System.out.println(message);
            }
        );
  }
  @Test
  void shouldReturnBadRequestToLongEmail(){
    String email = "adfgsdf" + "@"+"a".repeat(200)+"gmail.com";
    MyUserRequest user = new MyUserRequest("user","user",email);
    webTestClient.post().uri("/signup/"+"SELLER")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(user)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody(Map.class).value(
            response -> {
              String message = response.get("message").toString();
              assertThat(message).isEqualTo("email max 200 characters");
              System.out.println(message);
            }
        );
  }
  @Test
  void shouldReturnUserAlreadyExistsWhenEmailHasDifferentCase() {

    MyUserRequest user1 = new MyUserRequest("user1", "password", "usercase@gmail.com");
    webTestClient.post().uri("/signup/BUYER")
        .bodyValue(user1)
        .exchange()
        .expectStatus().isOk();


    MyUserRequest user2 = new MyUserRequest("user2", "password", "UserCase@gmail.com");
    webTestClient.post().uri("/signup/BUYER")
        .bodyValue(user2)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody(Map.class).value(response ->
            assertThat(response.get("message").toString())
                .isEqualTo("User with this email/username already exists")
        );
  }
  @Test
  void shouldCreateTwoAccountsWithDifferentRolesSameEmail(){
    MyUserRequest user1 = new MyUserRequest("user1", "password", "usercase@gmail.com");
    webTestClient.post().uri("/signup/BUYER")
        .bodyValue(user1)
        .exchange()
        .expectStatus().isOk();


    MyUserRequest user2 = new MyUserRequest("user2", "password", "usercase@gmail.com");
    webTestClient.post().uri("/signup/SELLER")
        .bodyValue(user2)
        .exchange()
        .expectStatus().isOk();
  }
  @Test
  void shouldReturnNullError(){
    MyUserRequest user = new MyUserRequest(null,null,null);
    webTestClient.post().uri("/signup/BUYER")
        .bodyValue(user)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody(Map.class).value(
            response -> {
              String message = response.get("message").toString();
             // assertThat(message).isNull();
              System.out.println(message);
            }
        );
  }
  @Test
  void shouldReturnNullErrorEmailOk(){
    MyUserRequest user = new MyUserRequest(null,null,"user@gmail.com");
    webTestClient.post().uri("/signup/BUYER")
        .bodyValue(user)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody(Map.class).value(
            response -> {
              String message = response.get("message").toString();
              // assertThat(message).isNull();
              System.out.println(message);
            }
        );
  }
}
