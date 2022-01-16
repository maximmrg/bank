package fr.miage.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Operation;
import fr.miage.bank.entity.User;
import fr.miage.bank.input.OperationInput;
import fr.miage.bank.service.AccountService;
import fr.miage.bank.service.CarteService;
import fr.miage.bank.service.OperationService;
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
import java.sql.Timestamp;
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
public class OperationTests {

    @LocalServerPort
    int port;

    @Autowired
    UserService userService;

    @Autowired
    AccountService accountService;

    @Autowired
    OperationService operationService;

    User userAdminTest;

    User userTest;
    Account accountTest;
    Account accountCredTest;
    String basePath;
    String pathAccount;

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

        this.basePath = "/users/" + user.getId() + "/accounts/" + accountTest.getIban() + "/operations/";
        this.pathAccount = "/users/" + user.getId() + "/accounts/" + accountTest.getIban() + "/";
    }

    @Test
    public void getOneTest() throws JSONException, IOException, URISyntaxException {

        Operation operation = new Operation(UUID.randomUUID().toString(), new Timestamp(System.currentTimeMillis()),
                "Loyer", 350, 1, accountTest, accountCredTest, "Commercant");

        operationService.createOperation(operation);

        String access_token = getToken(userTest.getEmail(), "password");

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get(basePath)
                .then()
                .extract().response();

        String jsonString = response.asString();
        assertThat(jsonString, containsString(operation.getLibelle()));
    }

    @Test
    public void getAllTest() throws JSONException, IOException, URISyntaxException {
        Operation operation = new Operation(UUID.randomUUID().toString(), new Timestamp(System.currentTimeMillis()),
                "Loyer", 350, 1, accountTest, accountCredTest, "Commercant");

        operationService.createOperation(operation);

        Operation operation2 = new Operation(UUID.randomUUID().toString(), new Timestamp(System.currentTimeMillis()),
                "Remboursement resto", 20, 1, accountTest, accountCredTest, "Personne");

        operationService.createOperation(operation2);

        Operation operation3 = new Operation(UUID.randomUUID().toString(), new Timestamp(System.currentTimeMillis()),
                "Remboursement resto", 25, 1, accountTest, accountCredTest, "Personne");

        operationService.createOperation(operation3);

        String access_token = getToken(userTest.getEmail(), "password");

        given()
                .header("Authorization", "Bearer " + access_token)
                .when().get(basePath)
                .then()
                .assertThat()
                .body("_embedded.operations.size()", equalTo(3));
    }

    @Test
    public void getAllByCategTest() throws JSONException, IOException, URISyntaxException {
        Operation operation = new Operation(UUID.randomUUID().toString(), new Timestamp(System.currentTimeMillis()),
                "Loyer", 350, 1, accountTest, accountCredTest, "Commercant");

        operationService.createOperation(operation);

        Operation operation2 = new Operation(UUID.randomUUID().toString(), new Timestamp(System.currentTimeMillis()),
                "Remboursement resto", 20, 1, accountTest, accountCredTest, "Personne");

        operationService.createOperation(operation2);

        Operation operation3 = new Operation(UUID.randomUUID().toString(), new Timestamp(System.currentTimeMillis()),
                "Remboursement resto", 25, 1, accountTest, accountCredTest, "Personne");

        operationService.createOperation(operation3);

        String access_token = getToken(userTest.getEmail(), "password");

        given()
                .header("Authorization", "Bearer " + access_token)
                .param("categ", "Personne")
                .when().get(basePath)
                .then()
                .assertThat()
                .body("_embedded.operations.size()", equalTo(3));
    }

    @Test
    public void postTest () throws JSONException, IOException, URISyntaxException {
        OperationInput operationInput = new OperationInput("Loyer", 350, accountCredTest.getIban(), "Commerçant");

        String access_token = getToken(userTest.getEmail(), "password");

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .body(toJsonString(operationInput))
                .contentType(ContentType.JSON)
                .when()
                .post(basePath)
                .then()
                .extract().response();

        String location = response.getHeader("Location");

        given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get(location)
                .then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void postDebiterTest() throws JSONException, IOException, URISyntaxException {
        OperationInput operationInput = new OperationInput("Loyer", 350, accountCredTest.getIban(), "Commerçant");

        String access_token = getToken(userTest.getEmail(), "password");

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .body(toJsonString(operationInput))
                .contentType(ContentType.JSON)
                .when()
                .post(basePath)
                .then()
                .extract().response();

        String location = response.getHeader("Location");

        Response res2 = given()
                .header("Authorization", "Bearer " + access_token)
                .when()
                .get(pathAccount)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().response();

        JSONObject jsonRes = new JSONObject(res2.asString());
        assertThat(jsonRes.get("solde"), equalTo(accountTest.getSolde() - operationInput.getMontant()));
    }

    @Test
    public void postWithNoSolde() throws IOException, JSONException, URISyntaxException {
        OperationInput operationInput = new OperationInput("Loyer", 600, accountCredTest.getIban(), "Commerçant");

        String access_token = getToken(userTest.getEmail(), "password");

        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .body(toJsonString(operationInput))
                .contentType(ContentType.JSON)
                .when()
                .post(basePath)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();
    }

    //--------
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
