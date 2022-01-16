package fr.miage.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Carte;
import fr.miage.bank.entity.Paiement;
import fr.miage.bank.entity.User;
import fr.miage.bank.input.PaiementInput;
import fr.miage.bank.service.*;
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

import javax.persistence.Table;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static fr.miage.bank.ConfigMethods.getToken;
import static fr.miage.bank.ConfigMethods.toJsonString;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = {"/data.sql"})
public class PaiementTests {

    @LocalServerPort
    int port;

    @Autowired
    UserService userService;

    @Autowired
    AccountService accountService;

    @Autowired
    CarteService carteService;

    @Autowired
    PaiementService paiementService;

    User userAdminTest;

    User userTest;
    Account accountTest;
    Account accountCredTest;
    Carte carteTest;
    Carte carteVirtualTest;

    String basePath;
    String pathAccount;
    String pathPaiements;

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

        Account account = new Account(iban.toString(), pays, "123456", 500, userTest);
        accountService.createAccount(account);

        iban = new Iban.Builder()
                .countryCode(CountryCode.getByCode(BankApplication.countries.get(pays)))
                .buildRandom();

        Account accountCred = new Account(iban.toString(), pays, "123456", 100, userTest);
        accountService.createAccount(accountCred);

        this.accountTest = account;
        this.accountCredTest = accountCred;

        Carte carte = new Carte(UUID.randomUUID().toString(), "1234567890123456", "1234", "123", false, false, 50, false, false, accountTest);
        carteService.createCarte(carte);

        Carte carteVirtual = new Carte(UUID.randomUUID().toString(), "1234567890123455", "1234", "123", false, false, 50, false, true, accountTest);
        carteService.createCarte(carteVirtual);

        this.carteTest = carte;
        this.carteVirtualTest = carteVirtual;

        this.basePath = "/paiements";
        this.pathAccount = "/users/" + user.getId() + "/accounts/" + accountTest.getIban();
        this.pathPaiements = this.pathAccount + "/cartes/" + carte.getId() + "/paiements/";
    }

    @Test
    public void getOneTest() throws JSONException, IOException, URISyntaxException {
        Paiement paiement = new Paiement(UUID.randomUUID().toString(), carteTest, 12, "France", accountCredTest, 1,
                new Timestamp(System.currentTimeMillis()), "Pizza", "commerce");
        paiementService.createPaiement(paiement);

        String access_token = getToken(userTest.getEmail(), "password");

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get(pathPaiements + paiement.getId())
                .then()
                .extract().response();

        String jsonString = response.asString();
        assertThat(jsonString, containsString(paiement.getLabel()));
    }

    @Test
    public void getAllTest() throws JSONException, IOException, URISyntaxException {
        Paiement paiement = new Paiement(UUID.randomUUID().toString(), carteTest, 12, "France", accountCredTest, 1,
                new Timestamp(System.currentTimeMillis()), "Pizza", "commerce");
        paiementService.createPaiement(paiement);

        Paiement paiement2 = new Paiement(UUID.randomUUID().toString(), carteTest, 25, "France", accountCredTest, 1,
                new Timestamp(System.currentTimeMillis()), "Pizza", "commerce");
        paiementService.createPaiement(paiement2);

        Paiement paiement3 = new Paiement(UUID.randomUUID().toString(), carteTest, 30, "France", accountCredTest, 1,
                new Timestamp(System.currentTimeMillis()), "Blablacar", "commerce");
        paiementService.createPaiement(paiement3);

        String access_token = getToken(userTest.getEmail(), "password");

        given()
                .header("Authorization", "Bearer " + access_token)
                .when().get(pathPaiements)
                .then()
                .assertThat()
                .body("_embedded.paiements.size()", equalTo(3));
    }

    @Test
    public void postPaiement() throws JSONException, IOException, URISyntaxException {
        PaiementInput paiementInput = new PaiementInput(50, "France", accountCredTest.getIban(),
                1, carteTest.getNumero(), carteTest.getCrypto(), userTest.getNom(),
                "Pizza", "commerce");

        String access_token = getToken(userTest.getEmail(), "password");

        Response response = given()
                .body(toJsonString(paiementInput))
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
    public void rejectWrongInputFormatPostPaiement() throws IOException, JSONException, URISyntaxException {
        PaiementInput paiementInput = new PaiementInput(50, "France", accountCredTest.getIban(),
                1, carteTest.getNumero(), "13", userTest.getNom(),
                "Pizza", "commerce");


        Response response = given()
                .body(toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post(basePath)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();
    }

    @Test
    public void rejectWrongInfoPostPaiement () throws JSONException, IOException, URISyntaxException {
        PaiementInput paiementInput = new PaiementInput(50, "France", accountCredTest.getIban(),
                1, carteTest.getNumero(), "111", userTest.getNom(),
                "Pizza", "commerce");


        Response response = given()
                .body(toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post(basePath)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();
    }

    @Test
    public void debiterComptePostPaiement() throws JSONException, IOException, URISyntaxException {
        PaiementInput paiementInput = new PaiementInput(50, "France", accountCredTest.getIban(),
                1, carteTest.getNumero(), carteTest.getCrypto(), userTest.getNom(),
                "Pizza", "commerce");

        String access_token = getToken(userTest.getEmail(), "password");

        Response response = given()
                .body(toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post(basePath)
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();

        Response accountRes = given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get(pathAccount)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        JSONObject jsonRes = new JSONObject(accountRes.asString());
        assertThat(jsonRes.get("solde"), equalTo(accountTest.getSolde() - paiementInput.getMontant()));
    }

    @Test
    public void postOnBlockedCard() throws JSONException, IOException, URISyntaxException {
        this.carteTest.setBloque(true);
        carteService.updateCarte(this.carteTest);

        PaiementInput paiementInput = new PaiementInput(50, "France", accountCredTest.getIban(),
                1, carteTest.getNumero(), carteTest.getCrypto(), userTest.getNom(),
                "Pizza", "commerce");

        String access_token = getToken(userTest.getEmail(), "password");

        Response response = given()
                .body(toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post(basePath)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();
    }

    @Test
    public void testWithPlafond() throws JSONException, IOException, URISyntaxException {
        PaiementInput paiementInput = new PaiementInput(70, "France", accountCredTest.getIban(),
                1, carteTest.getNumero(), carteTest.getCrypto(), userTest.getNom(),
                "Pizza", "commerce");

        String access_token = getToken(userTest.getEmail(), "password");

        Response response = given()
                .body(toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post(basePath)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();
    }

    @Test
    public void testWithVirtual() throws JSONException, IOException, URISyntaxException {
        PaiementInput paiementInput = new PaiementInput(50, "France", accountCredTest.getIban(),
                1, carteVirtualTest.getNumero(), carteVirtualTest.getCrypto(), userTest.getNom(),
                "Pizza", "commerce");

        String access_token = getToken(userTest.getEmail(), "password");

        Response response = given()
                .body(toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post(basePath)
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();

        Response resVirtual = given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get(pathAccount + "/cartes/" + carteVirtualTest.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        JSONObject jsonRes = new JSONObject(resVirtual.asString());
        assertThat(jsonRes.get("deleted"), equalTo(true));
    }
}
