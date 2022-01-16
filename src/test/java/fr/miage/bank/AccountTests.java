package fr.miage.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.User;
import fr.miage.bank.input.AccountInput;
import fr.miage.bank.service.AccountService;
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
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = {"/data.sql"})
public class AccountTests {

    @LocalServerPort
    int port;

    @Autowired
    UserService userService;

    @Autowired
    AccountService accountService;

    User userAdminTest;

    User userTest;
    String basePath;

    @BeforeEach
    public void setupContext() {
        RestAssured.port = port;
    }

    @BeforeEach
    public void initUser() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String dateInString = "1999-10-18";
        Date birthDate = formatter.parse(dateInString);

        User user = new User(UUID.randomUUID().toString(), "Marigliano", "Maxime",  birthDate, "123456", "0656897410",
                "maxime@mail.com", "$argon2id$v=19$m=4096,t=3,p=1$UpZ1oVVeUXwGb4CeqqMeog$aViAk5njkRuPGqY3+gkUNdUCIivAcP2Omvnm/MRBj7U");
        userService.createUser(user);

        this.userTest = user;
        this.basePath = "/users/" + user.getId() + "/accounts/";
    }

    @Test
    public void getOneTest() throws JSONException, IOException, URISyntaxException {

        String pays = "France";

        Iban iban = new Iban.Builder()
                .countryCode(CountryCode.getByCode(BankApplication.countries.get(pays)))
                .buildRandom();

        Account account = new Account(iban.toString(), pays, "1234", 100, userTest);
        accountService.createAccount(account);

        String access_token = getToken(userTest.getEmail(), "password");

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get(basePath + account.getIban())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        String jsonString = response.asString();
        assertThat(jsonString, containsString(iban.toString()));
    }

    @Test
    public void getAllTest() throws JSONException, IOException, URISyntaxException {
        String pays = "France";

        Iban iban = new Iban.Builder()
                .countryCode(CountryCode.getByCode(BankApplication.countries.get(pays)))
                .buildRandom();

        Iban iban2 = new Iban.Builder()
                .countryCode(CountryCode.getByCode(BankApplication.countries.get(pays)))
                .buildRandom();

        Account account = new Account(iban.toString(), pays, "1234", 100, userTest);
        Account account2 = new Account(iban2.toString(), pays, "12345", 100, userTest);

        accountService.createAccount(account);
        accountService.createAccount(account2);

        String access_token = getToken(userTest.getEmail(), "password");

        given()
                .header("Authorization", "Bearer " + access_token)
                .when().get(basePath)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .assertThat()
                .body("_embedded.accounts.size()", equalTo(2));
    }

    @Test
    public void postTest() throws JSONException, IOException, URISyntaxException {

        AccountInput accountInput = new AccountInput("France", "123456", 250);

        String access_token = getToken(userTest.getEmail(), "password");

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .body(this.toJsonString(accountInput))
                .contentType(ContentType.JSON)
                .when()
                .post(basePath)
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();

        String location = response.getHeader("Location");

        given()
                .header("Authorization", "Bearer " + access_token)
                .when().get(location).then().statusCode(HttpStatus.SC_OK);

    }

    @Test
    public void patchTest() throws JSONException, IOException, URISyntaxException {
        String pays = "France";

        Iban iban = new Iban.Builder()
                .countryCode(CountryCode.getByCode(BankApplication.countries.get(pays)))
                .buildRandom();

        Account account = new Account(iban.toString(), pays, "1234", 100, userTest);
        accountService.createAccount(account);

        String access_token = getToken(userTest.getEmail(), "password");

        AccountInput accountInput = new AccountInput("France", "123456", 250);

        //On change l'objet
        given()
                .header("Authorization", "Bearer " + access_token)
                .body(toJsonString(accountInput))
                .contentType(ContentType.JSON)
                .when()
                .patch(basePath + account.getIban())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        //Récupération de l'objet
        Response response = given()
                .when()
                .get(basePath)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        String stringResponse = response.asString();
        assertThat(stringResponse, containsString("123456"));
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
