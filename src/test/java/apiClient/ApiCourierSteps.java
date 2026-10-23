package apiClient;

import model.Courier;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class ApiCourierSteps {

    private static final String COURIER_PATH = "/api/v1/courier";
    private static final String COURIER_LOGIN_PATH = "/api/v1/courier/login";
    private static final String COURIER_DELETE_PATH = "/api/v1/courier/{id}";

    @Step("Создать курьера POST " + COURIER_PATH)
    public ValidatableResponse createCourierStep(Courier courier) {
        return given()
                .spec(ApiTestsConfig.getSpecification())
                .body(courier)
                .when()
                .post(COURIER_PATH)
                .then();
    }

    @Step("Залогинить курьера POST " + COURIER_LOGIN_PATH)
    public ValidatableResponse loginCourierStep(Courier courier) {
        return given()
                .spec(ApiTestsConfig.getSpecification())
                .body(courier)
                .when()
                .post(COURIER_LOGIN_PATH)
                .then();
    }

    @Step("Удалить курьера DELETE " + COURIER_DELETE_PATH)
    public ValidatableResponse deleteCourierStep(String id) {
        return given()
                .spec(ApiTestsConfig.getSpecification())
                .pathParam("id", id)
                .when()
                .delete(COURIER_DELETE_PATH)
                .then();
    }
}
