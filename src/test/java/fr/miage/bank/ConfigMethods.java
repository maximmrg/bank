package fr.miage.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

import static io.restassured.RestAssured.given;

public class ConfigMethods {

    public static String getToken(String email, String password) throws JSONException, IOException, URISyntaxException {
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

    public static String toJsonString(Object o) throws JsonProcessingException {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsString(o);
    }
}
