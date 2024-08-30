package courier;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import service.Service;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {

    private final CourierAPI courierApi = new CourierAPI();
    private Courier courierRandom;

    @Before
    public void setUp() {
        RestAssured.baseURI = Service.BASE_URL;
        courierRandom = (Courier) CourierData.generateRandom();
    }

    @After
    public void tearDown() {
        try {
            Response responseLogin = courierLogin(courierRandom);
            String courierId = responseLogin.then().extract().path("id").toString();
            courierDelete(courierId);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    @DisplayName("Логин незарегестрированного логина курьера")
    @Description("Проверка невозможности авторизоваться без регистрации")
    public void loginCourierWithoutLoginNameTest() {
        courierRandom.setLogin("");
        Response response = courierLogin(courierRandom);
        compareResultMessageToText(response, SC_BAD_REQUEST, "Недостаточно данных для входа");
    }

    @Test
    @DisplayName("Логин курьера без пароля")
    @Description("Проверка, невозможности авторизоваться без пароля")
    public void loginCourierWithoutPasswordTest() {
        courierRandom.setPassword("");
        Response response = courierLogin(courierRandom);
        compareResultMessageToText(response, SC_BAD_REQUEST, "Недостаточно данных для входа");
    }

    @Test
    @DisplayName("Логин несуществующего курьера")
    @Description("Проверка, что авторизация под несуществующим пользователем, запрос возвращает ошибку")
    public void loginCourierNotExistedTest() {
        Response response = courierLogin(courierRandom);
        compareResultMessageToText(response, SC_NOT_FOUND, "Учетная запись не найдена");
    }

    @Test
    @DisplayName("Успешный запрос возвращает id")
    @Description("Проверка, успешный запрос возвращает id")
    public void loginCourierPositiveTest() {
        courierCreate(courierRandom);
        Response response = courierLogin(courierRandom);
        compareIdNotNull(response);
    }

    // Метод для шага "Создать курьера":
    @Step("Create courier")
    public void courierCreate(Courier courier){
        Response response = courierApi.create(courier);
        printResponseBodyToConsole("Создание курьера: ", response, Service.NEED_DETAIL_LOG);
    }

    // Метод для шага "Авторизация курьера":
    @Step("Login courier")
    public Response courierLogin(Courier courier){
        Response response = courierApi.login(courier);
        printResponseBodyToConsole("Авторизация курьера: ", response, Service.NEED_DETAIL_LOG);
        return response;
    }

    // Метод для шага "Удалить курьера":
    @Step("Delete courier by id")
    public void courierDelete(String courierId){
        Response response = courierApi.delete(courierId);
        printResponseBodyToConsole("Удаление курьера: ", response, Service.NEED_DETAIL_LOG);
    }

    @Step("Compare id is not null")
    public void compareIdNotNull(Response response){
        response
                .then()
                .assertThat()
                .log().all()
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Step("Compare result message to something")
    public void compareResultMessageToText(Response response, int statusCode, String text){
        response
                .then()
                .log().all()
                .statusCode(statusCode)
                .and()
                .assertThat()
                .body("message", is(text));
    }

    // Метод для шага "Вывести тело ответа в консоль":
    @Step("Print response body to console")
    public void printResponseBodyToConsole(String headerText, Response response, boolean detailedLog){
        if (detailedLog)
            System.out.println(headerText + response.body().asString());
    }

}