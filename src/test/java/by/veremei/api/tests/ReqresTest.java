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

import static by.veremei.helpers.CustomApiListener.withCustomTemplates;
import static com.codeborne.selenide.Configuration.baseUrl;
import static io.qameta.allure.Allure.step;
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
        Response response = step("Делаем запрос", () -> given()
                .filter(withCustomTemplates())
                .when()
                .get(GET_LIST_USER_URL)
                .then()
                .log().all()
                .extract().response());
        step("Проверяем ответ сервера, что аватары содержат id пользователя", () -> {
            List<UserData> users = response.jsonPath().getList("data", UserData.class);
            users.forEach(x -> assertTrue(x.avatar().contains(x.id().toString())));
        });
    }

    @Test
    @DisplayName("Успешная регистрация пользователя")
    void successUserRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseUrl), Specifications.responseSpecOK200());
        Register user = new Register(regData.userEmail, regData.userPass);
        SuccessUserReg successUserReg = step("Делаем запрос", () -> given()
                .filter(withCustomTemplates())
                .body(user)
                .when()
                .post(POST_USER_REG_URL)
                .then()
                .log().all()
                .extract().as(SuccessUserReg.class));
        step("Проверяем, что id не равно null", () ->
                assertNotNull(successUserReg.id())
        );
        step("Проверяем, что token не равно null", () ->
                assertNotNull(successUserReg.token())
        );
        step("Проверяем id пользователя", () ->
                assertEquals(regData.userId, successUserReg.id())
        );
        step("Проверяем token пользователя", () ->
                assertEquals(regData.userToken, successUserReg.token())
        );
    }

    @Test
    @DisplayName("Неуспешная регистрация пользователя")
    void unSuccessUserRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseUrl), Specifications.responseSpecError400());
        Register user = new Register(regData.unSuccessUserEmail, regData.emptyUserPass);
        UnSuccessUserReg unSuccessUserReg = step("Делаем запрос", () -> given()
                .filter(withCustomTemplates())
                .body(user)
                .when()
                .post(POST_USER_REG_URL)
                .then()
                .log().all()
                .extract().as(UnSuccessUserReg.class));
        step("Проверяем наличие сообщения об ошибке", () ->
                assertEquals(regData.unSuccessErrorMessage, unSuccessUserReg.error())
        );
    }

    @Test
    @DisplayName("Пользователь не найден")
    void userNotFoundTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseUrl), Specifications.responseSpecError404());
        Response response = step("Делаем запрос", () -> given()
                .filter(withCustomTemplates())
                .when()
                .get(GET_USER_NOT_FOUND_URL)
                .then()
                .log().all()
                .extract().response());
        step("Проверяем, что статус ошибки равен 404", () ->
                assertEquals(404, response.getStatusCode())
        );
        step("Проверяем, что тело ответа пустое", () ->
                assertTrue(response.getBody().asString().replaceAll("[^a-zA-Z0-9]", "").isEmpty())
        );
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUserTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseUrl), Specifications.responseSpecUnique(204));
        step("Делаем запрос и проверяем, что статус код ответа равен 204", () -> {
            given()
                    .filter(withCustomTemplates())
                    .when()
                    .delete(DELETE_USER_URL)
                    .then()
                    .log().all();
        });
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    void successUserLoginTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseUrl), Specifications.responseSpecOK200());
        Login user = new Login(logData.userEmail, logData.userPass);
        SuccessUserLogin successUserLogin = step("Делаем запрос", () -> given()
                .filter(withCustomTemplates())
                .body(user)
                .when()
                .post(POST_USER_LOGIN_URL)
                .then()
                .log().all()
                .extract().as(SuccessUserLogin.class));
        step("Проверяем, что токен не равен null", () ->
                assertNotNull(successUserLogin.token())
        );
        step("Проверяем token пользователя", () ->
                assertEquals(logData.userToken, successUserLogin.token())
        );
    }

    @Test
    @DisplayName("Неуспешная авторизация пользователя")
    void unSuccessUserLoginTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseUrl), Specifications.responseSpecError400());
        Login user = new Login(logData.unSuccessUserEmail, logData.emptyPass);
        UnSuccessUserLogin unSuccessUserLogin = step("Делаем запрос", () -> given()
                .filter(withCustomTemplates())
                .body(user)
                .when()
                .post(POST_USER_LOGIN_URL)
                .then()
                .log().all()
                .extract().as(UnSuccessUserLogin.class));
        step("Проверяем наличие сообщения об ошибке", () ->
                assertEquals(logData.unSuccessErrorMessage, unSuccessUserLogin.error())
        );
    }
}
