package fr.miage.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.miage.bank.entity.User;
import fr.miage.bank.input.UserInput;
import fr.miage.bank.repository.UserRepository;
import fr.miage.bank.service.UserService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static fr.miage.bank.ConfigMethods.getToken;
import static fr.miage.bank.ConfigMethods.toJsonString;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = {"/data.sql"})
public class UserTests {

    @LocalServerPort
    int port;

    @Autowired
    UserService userService;

    @BeforeEach
    public void setupContext() {
        RestAssured.port = port;
    }

    @Test
    public void getOneTest() throws ParseException, IOException, JSONException, URISyntaxException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String dateInString = "1999-10-18";
        Date birthDate = formatter.parse(dateInString);

        User user = new User(UUID.randomUUID().toString(), "Marigliano", "Maxime",  birthDate, "123456", "0656897410",
                "maxime@mail.com", "$argon2id$v=19$m=4096,t=3,p=1$UpZ1oVVeUXwGb4CeqqMeog$aViAk5njkRuPGqY3+gkUNdUCIivAcP2Omvnm/MRBj7U");
        userService.createUser(user);

        userService.addRoleToUser(user, "ROLE_USER");

        String access_token = getToken(user.getEmail(), "password");

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when().get("/users/" + user.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString("Maxime"));
    }

    @Test
    public void getAllTest() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String dateInString = "1999-10-18";
        Date birthDate = formatter.parse(dateInString);


        User user = new User(UUID.randomUUID().toString(), "Marigliano", "Maxime",  birthDate, "123456", "0656897410",
                "maxime@mail.com", "$argon2id$v=19$m=4096,t=3,p=1$UpZ1oVVeUXwGb4CeqqMeog$aViAk5njkRuPGqY3+gkUNdUCIivAcP2Omvnm/MRBj7U");

        User user2 = new User(UUID.randomUUID().toString(), "Bristiel", "Elouan",  birthDate, "1234567", "0656897410",
                "elouan@mail.com", "$argon2id$v=19$m=4096,t=3,p=1$UpZ1oVVeUXwGb4CeqqMeog$aViAk5njkRuPGqY3+gkUNdUCIivAcP2Omvnm/MRBj7U");

        userService.createUser(user);
        userService.createUser(user2);

        when().get("/users/")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .assertThat()
                .body("_embedded.users.size()", equalTo(2));
    }

    @Test
    public void postTest() throws ParseException, IOException, JSONException, URISyntaxException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String dateInString = "1999-10-18";
        Date birthDate = formatter.parse(dateInString);

        UserInput userInput = new UserInput("Marigliano", "Maxime", birthDate, "123456", "0102030405", "max@mail.com", "password");

        Response response = given()
                .body(toJsonString(userInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/users")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();

        String access_token = getToken(userInput.getEmail(), "password");

        String location = response.getHeader("Location");
        given()
                .header("Authorization", "Bearer " + access_token)
                .when().get(location).then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void putTest() throws ParseException, IOException, JSONException, URISyntaxException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String dateInString = "1999-10-18";
        Date birthDate = formatter.parse(dateInString);

        User user = new User(UUID.randomUUID().toString(), "Bristiel", "Elouan",  birthDate, "1234567", "0656897410",
                "elouan@mail.com", "$argon2id$v=19$m=4096,t=3,p=1$UpZ1oVVeUXwGb4CeqqMeog$aViAk5njkRuPGqY3+gkUNdUCIivAcP2Omvnm/MRBj7U");

        userService.createUser(user);
        userService.addRoleToUser(user, "ROLE_USER");

        UserInput userInput = new UserInput("Marigliano", "Maxime", birthDate, "123456", "0102030405", "max@mail.com", "password");

        String access_token = getToken(user.getEmail(), "password");

        //On change les infos
        given()
                .header("Authorization", "Bearer " + access_token)
                .body(toJsonString(userInput))
                .contentType(ContentType.JSON)
                .when()
                .put("/users/" + user.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        //Nouveau Token
        access_token = getToken(userInput.getEmail(), "password");

        //Récupération de l'utilisateur modifié
        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when().get("/users/" + user.getId())
                .then()
                .extract().response();

        String stringResponse = response.asString();
        assertThat(stringResponse, containsString("Marigliano"));
    }

    @Test
    public void patchTest() throws ParseException, JSONException, IOException, URISyntaxException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String dateInString = "1999-10-18";
        Date birthDate = formatter.parse(dateInString);

        User user = new User(UUID.randomUUID().toString(), "Bristiel", "Elouan",  birthDate, "1234567", "0656897410",
                "elouan@mail.com", "$argon2id$v=19$m=4096,t=3,p=1$UpZ1oVVeUXwGb4CeqqMeog$aViAk5njkRuPGqY3+gkUNdUCIivAcP2Omvnm/MRBj7U");
        userService.createUser(user);
        userService.addRoleToUser(user, "ROLE_USER");

        String access_token = getToken(user.getEmail(), "password");

        String jsonString = "{" +
                "\"prenom\" : \"Tom\"" +
                "}";

        Response resPatch = given()
                .header("Authorization", "Bearer " + access_token)
                .body(jsonString)
                .contentType(ContentType.JSON)
                .when()
                .patch("/users/" + user.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        //Récupération de l'utilisateur modifié
        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when().get("/users/" + user.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        String stringResponse = response.asString();
        assertThat(stringResponse, containsString("Tom"));
    }
}
