package apiclient;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class ApiTestsConfig {
    public static RequestSpecification getSpecification(){
        return new RequestSpecBuilder()
                .setBaseUri("https://qa-scooter.education-services.ru/")
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    public static ResponseSpecification getResponseSpecification(){
        return new ResponseSpecBuilder()
                .log(LogDetail.ALL)
                .build();
    }
}