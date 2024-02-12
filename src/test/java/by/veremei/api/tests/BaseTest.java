package by.veremei.api.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
    @BeforeAll
    public static void setupAll() {
        RestAssured.baseURI = "https://reqres.in/";
    }
}
