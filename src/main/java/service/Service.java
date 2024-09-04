package service;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.builder.RequestSpecBuilder;

public class Service {

    public static final String BASE_URL = "http://qa-scooter.praktikum-services.ru/";
    public final static boolean NEED_DETAIL_LOG = true;


    protected RequestSpecification getBaseSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .build();
    }

}