package order;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import service.Service;

import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest {

    private final List<String> color;
    private final OrderAPI orderApi = new OrderAPI();
    private Order order;
    private Response response;

    public OrderCreateTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "{index}: Цвет самоката: {0}")
    public static Object[][] createOrderWithDifferentColors() {
        return new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of()},
                {List.of("BLACK", "GREY")},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = Service.BASE_URL;
        order = new Order("Семен", "Семенов", "улица Цветочная, 213", "4", "+7 999 999 99 99", 5, "2024-08-29", "аааа", color);
    }

    @After
    public void tearDown() {
        try {
            String orderId = response.then().extract().path("track").toString();
            orderApi.cancelOrder(orderId);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    @DisplayName("Создание заказа с разными цветами")
    @Description("Проверка, что можно создать заказ с переданными наборами цветов (в параметрах)")
    public void paramCreateOrderTest() {
        response = orderCreate(order);
        compareTrackNotNull(response);
    }

    @Step("Create order")
    public Response orderCreate(Order order){
        response = orderApi.createOrder(order);
        printResponseBodyToConsole("Создание заказа: ", response, Service.NEED_DETAIL_LOG);
        return response;
    }

    @Step("Compare track is not null")
    public void compareTrackNotNull(Response response){
        response
                .then()
                .assertThat()
                .statusCode(SC_CREATED)
                .and()
                .body("track", notNullValue());
    }

    @Step("Print response body to console")
    public void printResponseBodyToConsole(String headerText, Response response, boolean detailedLog){
        if (detailedLog)
            System.out.println(headerText + response.body().asString());
    }

}