package by.veremei.api.tests;

import by.veremei.api.models.login.Login;
import by.veremei.api.models.login.SuccessUserLogin;
import by.veremei.api.models.login.UnSuccessUserLogin;
import by.veremei.api.models.registration.Register;
import by.veremei.api.models.registration.SuccessUserReg;
import by.veremei.api.models.registration.UnSuccessUserReg;
import by.veremei.api.spec.Specifications;
import by.veremei.api.models.users.UserData;
import by.veremei.api.data.LoginData;
import by.veremei.api.data.RegisterData;
import by.veremei.pages.UserPage;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("API тесты")
@Tag("API")
public class ReqresTest extends BaseTest {
    RegisterData regData = new RegisterData();
    LoginData logData = new LoginData();

    @Test
    @Feature("Управление пользователями")
    @Story("Список пользователей")
    @Owner("tg - @Veremeioleg")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Аватары содержат id пользователей")
    void checkAvatarContainsIdTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseURI), Specifications.responseSpecOK200());
        Response response = step("Делаем запрос на получение списка пользователей", () -> given()
                .when()
                .get(UserPage.GET_LIST_USER_URL)
                .then()
                .log().all()
                .extract().response());
        step("Проверяем ответ сервера, что аватары содержат id пользователя", () -> {
            List<UserData> users = response.jsonPath().getList("data", UserData.class);
            users.forEach(x -> assertTrue(x.getAvatar().contains(x.getId().toString())));
        });
    }

    @Test
    @Feature("Управление пользователями")
    @Story("Регистрация нового пользователя")
    @Owner("tg - @Veremeioleg")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Успешная регистрация пользователя")
    void successUserRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseURI), Specifications.responseSpecOK200());
        Register user = new Register(regData.userEmail, regData.userPass);
        SuccessUserReg successUserReg = step("Отправляем запрос на регистрацию пользователя", () -> given()
                .body(user)
                .when()
                .post(UserPage.POST_USER_REG_URL)
                .then()
                .log().all()
                .extract().as(SuccessUserReg.class));
        step("Проверяем, что id не равно null", () ->
                assertNotNull(successUserReg.getId())
        );
        step("Проверяем, что token не равно null", () ->
                assertNotNull(successUserReg.getToken())
        );
        step("Проверяем id пользователя", () ->
                    assertEquals(regData.userId, successUserReg.getId())
        );
        step("Проверяем token пользователя", () ->
                assertEquals(regData.userToken, successUserReg.getToken())
        );
    }

    @Test
    @Feature("Управление пользователями")
    @Story("Регистрация нового пользователя")
    @Owner("tg - @Veremeioleg")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Неуспешная регистрация пользователя")
    void unSuccessUserRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseURI), Specifications.responseSpecError400());
        Register user = new Register(regData.unSuccessUserEmail, regData.emptyUserPass);
        UnSuccessUserReg unSuccessUserReg = step("Отправляем запрос на регистрацию пользователя с некорректными данными", () -> given()
                .body(user)
                .when()
                .post(UserPage.POST_USER_REG_URL)
                .then()
                .log().all()
                .extract().as(UnSuccessUserReg.class));
        step("Проверяем наличие сообщения об ошибке", () ->
                assertEquals(regData.unSuccessErrorMessage, unSuccessUserReg.getError())
        );
    }

    @Test
    @Feature("Управление пользователями")
    @Story("Информация о пользователе")
    @Owner("tg - @Veremeioleg")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Пользователь не найден")
    void userNotFoundTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseURI), Specifications.responseSpecError404());
        Response response = step("Делаем запрос на получение информации о несуществующем пользователе", () -> given()
                .when()
                .when()
                .get(UserPage.GET_USER_NOT_FOUND_URL)
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
    @Feature("Управление пользователями")
    @Story("Удаление пользователя")
    @Owner("tg - @Veremeioleg")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Удаление пользователя")
    void deleteUserTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseURI), Specifications.responseSpecUnique(204));
        step("Делаем запрос на удаление пользователя и проверяем, что статус код ответа равен 204", () -> {
            given()
                    .when()
                    .delete(UserPage.DELETE_USER_URL)
                    .then()
                    .log().all()
                    .statusCode(204);
        });
    }

    @Test
    @Feature("Управление пользователями")
    @Story("Авторизация пользователя")
    @Owner("tg - @Veremeioleg")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Успешная авторизация пользователя")
    void successUserLoginTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseURI), Specifications.responseSpecOK200());
        Login user = new Login(logData.userEmail, logData.userPass);
        SuccessUserLogin successUserLogin = step("Отправляем запрос на авторизацию пользователя с корректными учетными данными", () -> given()
                .body(user)
                .when()
                .post(UserPage.POST_USER_LOGIN_URL)
                .then()
                .log().all()
                .extract().as(SuccessUserLogin.class));
        step("Проверяем, что токен не равен null", () ->
                assertNotNull(successUserLogin.getToken())
        );
        step("Проверяем token пользователя", () ->
                assertEquals(logData.userToken, successUserLogin.getToken())
        );
    }

    @Test
    @Feature("Управление пользователями")
    @Story("Авторизация пользователя")
    @Owner("tg - @Veremeioleg")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Неуспешная авторизация пользователя")
    void unSuccessUserLoginTest() {
        Specifications.installSpecification(Specifications.requestSpec(baseURI), Specifications.responseSpecError400());
        Login user = new Login(logData.unSuccessUserEmail, logData.emptyPass);
        UnSuccessUserLogin unSuccessUserLogin = step("Отправляем запрос на авторизацию пользователя с некорректными учетными данными", () -> given()
                .body(user)
                .when()
                .post(UserPage.POST_USER_LOGIN_URL)
                .then()
                .log().all()
                .extract().as(UnSuccessUserLogin.class));
        step("Проверяем наличие сообщения об ошибке", () ->
                assertEquals(logData.unSuccessErrorMessage, unSuccessUserLogin.getError())
        );
    }


}
