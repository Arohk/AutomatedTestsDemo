package apitests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.jayway.jsonpath.JsonPath;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class APITests {


    static String loginToken;

    @BeforeTest
    public void loginTest() throws IOException {
        // create new LoginPOJO class object named login
        LoginPOJO login = new LoginPOJO();

        FileReader reader = new FileReader("credentials.properties");
        Properties properties = new Properties();

        properties.load(reader);

        System.out.println(properties.getProperty("user"));
        System.out.println(properties.getProperty("password"));


        // set the login credentials to our login object
        login.setUsernameOrEmail("test52");
        login.setPassword("test52");

        baseURI = "http://training.skillo-bg.com:3100";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(login)
                .when()
                .post("/users/login");
        response
                .then()
                .statusCode(201);

        // convert the response body json into a string
        String loginResponseBody = response.getBody().asString();

        loginToken = JsonPath.parse(loginResponseBody).read("$.token");

        System.out.println("LOGIN BODY IS: ");
        System.out.println(loginResponseBody);
    }

    @Test
    public void registerNewUser(){

        RegisterPOJO register = new RegisterPOJO();

        // logic for unique username and password
        Date date = new Date();

        register.setUsername("Niki" + date.getTime());
        register.setEmail("N" + date.getTime() + "@b.b");
        register.setBirthDate("12.22.1985");
        register.setPassword("Abc123456");
//        register.setPublicInfo("Hello!");

        ValidatableResponse validatableResponse = given()
                .header("Content-Type", "application/json")
                .body(register)
                .when()
                .post("/users")
                .then()
                .log()
                .all()
                .statusCode(201);
    }

    @Test
    public void likePost() {
        // create an object of ActionsPOJO class and add value for the fields
        ActionsPOJO likePost = new ActionsPOJO();
        likePost.setAction("likePost");

        ValidatableResponse validatableResponse = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .body(likePost)
                .when()
                .patch("/posts/4626")
                .then()
                .body("user.username", equalTo("test52"))
                .log()
                .all();

        String myUser = validatableResponse.extract().path("user.username");


//        System.out.println("Extracted validatable response item is:");
//        System.out.println((String) validatableResponse.extract().path("post.user.username"));


    }

    @Test
    public void commentPost() {
        ActionsPOJO commentPost = new ActionsPOJO();
        commentPost.setContent("My New Comment!");

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .body(commentPost)
                .when()
                .post("/posts/4626/comment")
                .then()
                .body("content", equalTo("My New Comment!"))
                .log()
                .all()
                .statusCode(201);
    }

    @Test
    public void likePostVerifications() {
        // create an object of ActionsPOJO class and add value for the fields
        ActionsPOJO likePost = new ActionsPOJO();
        likePost.setAction("likePost");

        RequestSpecification request = given();
        request.header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .body(likePost)
                .when()
                .patch("/posts/4626")
                .then()
                .body("post.id", equalTo(4626))
                .log()
                .all();
    }

    @Test
    public void getUsersPosts() {
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .param("skip", 0)
                .param("take", 15)
                .when()
                .get("/users/2394/posts");


        response.then()
                .contentType(ContentType.JSON)
//                .body("user.username", equalTo("test52"))
                .log()
                .all();

    }

}