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

       /* Response response = given()
                .when().get("/users/")
                .then()
                .extract().response();

        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString("Elouan"));*/

        when().get("/users/")
                .then()
                .assertThat()
                .body("size()", equalTo(2));
    }

    @Test
    public void postTest() throws ParseException, IOException, JSONException, URISyntaxException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String dateInString = "1999-10-18";
        Date birthDate = formatter.parse(dateInString);

        UserInput userInput = new UserInput("Marigliano", "Maxime", birthDate, "123456", "0102030405", "max@mail.com", "password");

        Response response = given()
                .body(this.toJsonString(userInput))
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

    private String toJsonString(Object o) throws JsonProcessingException {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsString(o);
    }

    @Test
    public void putTest() throws ParseException, IOException, JSONException, URISyntaxException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String dateInString = "1999-10-18";
        Date birthDate = formatter.parse(dateInString);

        User user = new User(UUID.randomUUID().toString(), "Bristiel", "Elouan",  birthDate, "1234567", "0656897410",
                "elouan@mail.com", "$argon2id$v=19$m=4096,t=3,p=1$UpZ1oVVeUXwGb4CeqqMeog$aViAk5njkRuPGqY3+gkUNdUCIivAcP2Omvnm/MRBj7U");
        userService.createUser(user);

        UserInput userInput = new UserInput("Marigliano", "Maxime", birthDate, "123456", "0102030405", "max@mail.com", "password");

        String access_token = getToken(user.getEmail(), "password");

        //On change les infos
        given()
                .header("Authorization", "Bearer " + access_token)
                .body(this.toJsonString(userInput))
                .contentType(ContentType.JSON)
                .when()
                .put("/users/" + user.getId())
                .then()
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
    public void patchTest() {

    }

    private String getToken(String email, String password) throws JSONException, IOException, URISyntaxException {
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);

        String json_body = String.format("email=%s&password=%s", email, "password");

        Response response = given()
                .with()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(json_body)
                .when()
                .post("/login")
                .then()
                .extract()
                .response();

        String stringRes = response.asString();
        JSONObject jsonRes = new JSONObject(stringRes);
        return jsonRes.getString("access_token");
    }
}
