package courier;

import service.Service;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.is;

public class CourierCreateTest {

    private final CourierAPI courierApi = new CourierAPI();
    private Courier courier;

    @Before
    public void setUp() {
        RestAssured.baseURI = Service.BASE_URL;
        courier = (Courier) CourierData.generateRandom();
    }

    @After
    public void tearDown() {
        try {
            Response responseLogin = courierLogin(courier);
            String courierId = responseLogin.then().extract().path("id").toString();
            courierDelete(courierId);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    @DisplayName("Регистрация курьера")
    @Description("Проверка, создания курьера с корректыми введенными данными")
    public void createNewCourierPositiveTest() {
        Response response = courierCreate(courier);
        compareResultToTrue(response, SC_CREATED);
    }

    @Test
    @DisplayName("Регистрация курьера без логина")
    @Description("Проверка, невозможности создать курьера без указания логина")
    public void createNewCourierNoLoginTest() {
        courier.setLogin("");
        Response response = courierCreate(courier);
        compareResultMessageToText(response, SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @DisplayName("Регистрация имеющегося курьера")
    @Description("Проверка, невозможности создать курьера, который уже существует")
    public void createNewDuplicateCourierTest() {
        // От первого ответ не нужен
        courierCreate(courier);
        Response response = courierCreate(courier);
        compareResultMessageToText(response, SC_CONFLICT, "Этот логин уже используется. Попробуйте другой.");
    }

    // Метод для шага "Создать курьера":
    @Step("Create courier")
    public Response courierCreate(Courier courier){
        Response response = courierApi.create(courier);
        printResponseBodyToConsole("Создание курьера: ", response, Service.NEED_DETAIL_LOG);
        return response;
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

    @Step("Compare result to true")
    public void compareResultToTrue(Response response, int statusCode){
        response
                .then()
                .assertThat()
                .log().all()
                .statusCode(statusCode)
                .body("ok", is(true));
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

    //Вывод тела ответа в консоль:
    @Step("Print response body to console")
    public void printResponseBodyToConsole(String headerText, Response response, boolean detailedLog){
        if (detailedLog)
            System.out.println(headerText + response.body().asString());
    }

}