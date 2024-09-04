package courier;
import service.Service;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class CourierAPI extends Service {
    private static final String COURIER_API_PATH = "/api/v1/courier";
    private static final String COURIER_API_LOGIN_PATH = "/api/v1/courier/login";
    private static final String COURIER_API_DELETE_PATH = "/api/v1/courier/";

    public Response login(Courier courier) {
        return given()
                .spec(getBaseSpecification())
                .and()
                .body(courier)
                .when()
                .post(COURIER_API_LOGIN_PATH);
    }

    public Response create(Courier courier) {
        return given()
                .spec(getBaseSpecification())
                .and()
                .body(courier)
                .log().all()
                .when()
                .post(COURIER_API_PATH);
    }

    public Response delete(String id) {
        return given()
                .spec(getBaseSpecification())
                .delete(COURIER_API_DELETE_PATH + id);
    }
}