package apiClient;

import model.Order;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class ApiOrderClient {

    private static final String ORDERS_PATH = "/api/v1/orders";
    private static final String ORDERS_CANCEL_PATH = "/api/v1/orders/cancel";

    @Step("Создать заказ POST " + ORDERS_PATH)
    public ValidatableResponse createOrder(Order order) {
        return given()
                .spec(ApiTestsConfig.getSpecification())
                .body(order)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    @Step("Отменить заказ PUT " + ORDERS_CANCEL_PATH)
    public ValidatableResponse cancelOrder(String track) {
        return given()
                .spec(ApiTestsConfig.getSpecification())
                .queryParam("track", track)
                .when()
                .put(ORDERS_CANCEL_PATH)
                .then();
    }

    @Step("Получить список заказов GET " + ORDERS_PATH)
    public ValidatableResponse getOrderList() {
        return given()
                .spec(ApiTestsConfig.getSpecification())
                .when()
                .get(ORDERS_PATH)
                .then();
    }
}
