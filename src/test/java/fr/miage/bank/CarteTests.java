package fr.miage.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Carte;
import fr.miage.bank.entity.User;
import fr.miage.bank.input.CarteInput;
import fr.miage.bank.service.AccountService;
import fr.miage.bank.service.CarteService;
import fr.miage.bank.service.UserService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
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
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = {"/data.sql"})
public class CarteTests {

    @LocalServerPort
    int port;

    @Autowired
    UserService userService;

    @Autowired
    AccountService accountService;

    @Autowired
    CarteService carteService;

    User userAdminTest;

    User userTest;
    Account accountTest;
    String basePath;

    @BeforeEach
    public void setupContext() {
        RestAssured.port = port;
    }

    @BeforeEach
    public void initUserAndAccount() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String dateInString = "1999-10-18";
        Date birthDate = formatter.parse(dateInString);

        User user = new User(UUID.randomUUID().toString(), "Marigliano", "Maxime",  birthDate, "123456", "0656897410",
                "maxime@mail.com", "$argon2id$v=19$m=4096,t=3,p=1$UpZ1oVVeUXwGb4CeqqMeog$aViAk5njkRuPGqY3+gkUNdUCIivAcP2Omvnm/MRBj7U");
        userService.createUser(user);

        this.userTest = user;

        String pays = "France";

        Iban iban = new Iban.Builder()
                .countryCode(CountryCode.getByCode(BankApplication.countries.get(pays)))
                .buildRandom();

        Account account = new Account(iban.toString(), pays, "123456", 100, userTest);
        accountService.createAccount(account);

        this.accountTest = account;


        this.basePath = "/users/" + user.getId() + "/accounts/" + accountTest.getIban() + "/cartes/";
    }

    @Test
    public void getOneTest() throws JSONException, IOException, URISyntaxException {

        Carte carte = new Carte(UUID.randomUUID().toString(), "12345678", "1234", "123", false, false, 50, false, false, accountTest);
        carteService.createCarte(carte);

        String access_token = getToken(userTest.getEmail(), "password");

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get(basePath + carte.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        String jsonString = response.asString();;
        assertThat(jsonString, containsString(carte.getNumero()));
    }

    @Test
    public void getAllTest() throws JSONException, IOException, URISyntaxException {
        Carte carte = new Carte(UUID.randomUUID().toString(), "12345678", "1234", "123", false, false, 50, false, false, accountTest);
        carteService.createCarte(carte);

        Carte carte2 = new Carte(UUID.randomUUID().toString(), "12345678910", "1234", "123", false, false, 50, false, false, accountTest);
        carteService.createCarte(carte2);

        Carte carte3 = new Carte(UUID.randomUUID().toString(), "12345678910", "1234", "123", false, false, 50, false, false, accountTest);
        carteService.createCarte(carte3);

        String access_token = getToken(userTest.getEmail(), "password");

        given()
                .header("Authorization", "Bearer " + access_token)
                .when().get(basePath)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .assertThat()
                .body("_embedded.cartes.size()", equalTo(3));
    }

    @Test
    public void postTest() throws JSONException, IOException, URISyntaxException {
        CarteInput carteInput = new CarteInput(false, 50, false, false);

        String access_token = getToken(userTest.getEmail(), "password");

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .body(this.toJsonString(carteInput))
                .contentType(ContentType.JSON)
                .when()
                .post(basePath)
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();

        String location = response.getHeader("Location");

        given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get(location)
                .then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void blockCardTest() throws JSONException, IOException, URISyntaxException {
        Carte carte = new Carte(UUID.randomUUID().toString(), "12345678", "1234", "123", false, false, 50, false, false, accountTest);
        carteService.createCarte(carte);

        String access_token = getToken(userTest.getEmail(), "password");

        given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .post(basePath + carte.getId() + "/block")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .extract().response();

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get(basePath + carte.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        JSONObject jsonRes = new JSONObject(response.asString());
        assertThat(jsonRes.get("bloque"), equalTo(true));
    }

    @Test
    public void localisationCardTest() throws JSONException, IOException, URISyntaxException {
        Carte carte = new Carte(UUID.randomUUID().toString(), "12345678", "1234", "123", false, false, 50, false, false, accountTest);
        carteService.createCarte(carte);

        String access_token = getToken(userTest.getEmail(), "password");

        given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .post(basePath + carte.getId() + "/activeLocalisation")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .extract().response();

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get(basePath + carte.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        JSONObject jsonRes = new JSONObject(response.asString());
        assertThat(jsonRes.get("localisation"), equalTo(true));
    }

    @Test
    public void setContactCardTest() throws JSONException, IOException, URISyntaxException {
        Carte carte = new Carte(UUID.randomUUID().toString(), "12345678", "1234", "123", false, false, 50, false, false, accountTest);
        carteService.createCarte(carte);

        String access_token = getToken(userTest.getEmail(), "password");

        given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .post(basePath + carte.getId() + "/setContact")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .extract().response();

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get(basePath + carte.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        JSONObject jsonRes = new JSONObject(response.asString());
        assertThat(jsonRes.get("sansContact"), equalTo(true));
    }

    @Test
    public void unsetContactCardTest() throws JSONException, IOException, URISyntaxException {
        Carte carte = new Carte(UUID.randomUUID().toString(), "12345678", "1234", "123", false, false, 50, true, false, accountTest);
        carteService.createCarte(carte);

        String access_token = getToken(userTest.getEmail(), "password");

        given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .post(basePath + carte.getId() + "/unsetContact")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .extract().response();

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get(basePath + carte.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        JSONObject jsonRes = new JSONObject(response.asString());
        assertThat(jsonRes.get("sansContact"), equalTo(false));
    }

    @Test
    public void setPlafondCardTest() throws JSONException, IOException, URISyntaxException {
        Carte carte = new Carte(UUID.randomUUID().toString(), "12345678", "1234", "123", false, false, 50, true, false, accountTest);
        carteService.createCarte(carte);

        String access_token = getToken(userTest.getEmail(), "password");

        given()
                .header("Authorization", "Bearer " + access_token)
                .param("plafond", 100)
                .when()
                .post(basePath + carte.getId() + "/setPlafond")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .extract().response();

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get(basePath + carte.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        JSONObject jsonRes = new JSONObject(response.asString());
        assertThat(jsonRes.get("plafond"), equalTo(100.0));
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

    private String toJsonString(Object o) throws JsonProcessingException {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsString(o);
    }
}
