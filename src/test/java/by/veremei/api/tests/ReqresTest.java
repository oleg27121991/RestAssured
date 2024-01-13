package by.veremei.api.tests;

import by.veremei.api.login.Login;
import by.veremei.api.login.SuccessUserLogin;
import by.veremei.api.login.UnSuccessUserLogin;
import by.veremei.api.registration.Register;
import by.veremei.api.registration.SuccessUserReg;
import by.veremei.api.registration.UnSuccessUserReg;
import by.veremei.config.ConfigReader;
import by.veremei.config.ProjectConfiguration;
import by.veremei.config.web.WebConfig;
import by.veremei.api.spec.Specifications;
import by.veremei.api.users.UserData;
import by.veremei.api.data.LoginData;
import by.veremei.api.data.RegisterData;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.List;

import static com.codeborne.selenide.Configuration.baseUrl;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("API tests")
public class ReqresTest {
    RegisterData regData = new RegisterData();
    LoginData logData = new LoginData();
    private final static String GET_LIST_USER_URL = "api/users?page=2",
                                POST_USER_REG_URL = "api/register",
                                GET_USER_NOT_FOUND_URL = "api/users/23",
                                DELETE_USER_URL = "api/users/2",
                                POST_USER_LOGIN_URL = "api/login";

    @BeforeEach
    public void setup() {
        ConfigReader configReader = ConfigReader.Instance;
        WebConfig webConfig = configReader.read();

        ProjectConfiguration projectConfiguration = new ProjectConfiguration(webConfig);
        projectConfiguration.webConfig();
    }

    @Test
    @DisplayName("Аватары содержат id пользователей")
    void checkAvatarContainsIdTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseUrl), Specifications.responseSpecOK200());
        Response response = given()
                .when()
                .get(GET_LIST_USER_URL)
                .then()
                .log().all()
                .extract().response();
        List<UserData> users = response.jsonPath().getList("data", UserData.class);
        users.forEach(x -> assertTrue(x.getAvatar().contains(x.getId().toString())));
    }

    @Test
    @DisplayName("Успешная регистрация пользователя")
    void successUserRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseUrl), Specifications.responseSpecOK200());
        Register user = new Register(regData.userEmail, regData.userPass);
        SuccessUserReg successUserReg = given()
                .body(user)
                .when()
                .post(POST_USER_REG_URL)
                .then()
                .log().all()
                .extract().as(SuccessUserReg.class);
        assertNotNull(successUserReg.getId());
        assertNotNull(successUserReg.getToken());
        assertEquals(regData.userId, successUserReg.getId());
        assertEquals(regData.userToken, successUserReg.getToken());
    }

    @Test
    @DisplayName("Неуспешная регистрация пользователя")
    void unSuccessUserRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseUrl), Specifications.responseSpecError400());
        Register user = new Register(regData.unSuccessUserEmail, regData.emptyUserPass);
        UnSuccessUserReg unSuccessUserReg = given()
                .body(user)
                .when()
                .post(POST_USER_REG_URL)
                .then()
                .log().all()
                .extract().as(UnSuccessUserReg.class);
        assertEquals(regData.unSuccessErrorMessage, unSuccessUserReg.getError());
    }

    @Test
    @DisplayName("Пользователь не найден")
    void userNotFoundTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseUrl), Specifications.responseSpecError404());
        Response response = given()
                .when()
                .get(GET_USER_NOT_FOUND_URL)
                .then()
                .log().all()
                .extract().response();
        assertEquals(404, response.getStatusCode());
        assertTrue(response.getBody().asString().replaceAll("[^a-zA-Z0-9]", "").isEmpty());
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUserTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseUrl), Specifications.responseSpecUnique(204));
        given()
                .when()
                .delete(DELETE_USER_URL)
                .then()
                .log().all();
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    void successUserLoginTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseUrl), Specifications.responseSpecOK200());
        Login user = new Login(logData.userEmail, logData.userPass);
        SuccessUserLogin successUserLogin = given()
                .body(user)
                .when()
                .post(POST_USER_LOGIN_URL)
                .then()
                .log().all()
                .extract().as(SuccessUserLogin.class);
        assertNotNull(successUserLogin.getToken());
        assertEquals(logData.userToken, successUserLogin.getToken());
    }

    @Test
    @DisplayName("Неуспешная авторизация пользователя")
    void unSuccessUserLoginTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseUrl), Specifications.responseSpecError400());
        Login user = new Login(logData.unSuccessUserEmail, logData.emptyPass);
        UnSuccessUserLogin unSuccessUserLogin = given()
                .body(user)
                .when()
                .post(POST_USER_LOGIN_URL)
                .then()
                .log().all()
                .extract().as(UnSuccessUserLogin.class);
        assertEquals(logData.unSuccessErrorMessage, unSuccessUserLogin.getError());
    }
}
