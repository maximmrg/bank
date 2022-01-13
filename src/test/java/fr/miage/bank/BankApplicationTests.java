package fr.miage.bank;

import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static io.restassured.RestAssured.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BankApplicationTests {

	@LocalServerPort
	int port;

	@BeforeEach
	public void setupContext() {
		RestAssured.port = port;
	}

	@Test
	void pingAPI() {
		when().get("/users").then().statusCode(HttpStatus.SC_OK);
	}

}
