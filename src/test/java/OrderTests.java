import model.Order;
import io.qameta.allure.Allure;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderTests {

    private List<Order> createdOrders;

    @BeforeEach
    public void setUp() {
        baseURI = "https://qa-scooter.education-services.ru/";
        createdOrders = new ArrayList<>();
    }

    @Step("Создать заказ POST /api/v1/orders")
    public Order createOrder(Order order){
        Integer track = given()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post("/api/v1/orders")
                .then()
                .log().body()
                .statusCode(201)
                .extract()
                .path("track");

        assertNotNull(track, "track не должен быть null");

        order.setTrack(track);
        createdOrders.add(order);

        return order;
    }

    public void cancelOrder(String track) {
        Allure.step("Отменить заказ PUT /api/v1/orders/cancel track: " + track);

        given()
                .contentType(ContentType.JSON)
                .queryParam("track", track)
                .when()
                .put("/api/v1/orders/cancel")
                .then()
                .log().body()
                .statusCode(200);
    }

    @AfterEach
    public void TearDown() {
        System.out.println("TearDown createdOrders.size(): " + createdOrders.size());
        for (Order order : createdOrders) {
            try {
                cancelOrder(order.getTrack().toString());
            } catch (Exception e) {
                System.out.println("TearDown Exception error: " + e.getMessage());
            }
        }
    }


    @Test
    @DisplayName("Создание заказа; Тело ответа содержит track.")
    public void createOrderTest(){
        Order order = new Order(
                "Иван"+ UUID.randomUUID(),
                "Иванов",
                "ул. Пушкина, д. 10",
                "Черкизовская",
                "+7 999 111 22 33",
                5,
                "2026-11-25",
                "Позвонить за час до доставки",
                List.of("BLACK")
        );

        Allure.step("вызов POST /api/v1/orders");
        Integer track = given()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post("/api/v1/orders")
                .then()
                .log().body()
                .statusCode(201)
                .extract()
                .path("track");

        assertNotNull(track, "track не должен быть null");

        order.setTrack(track);
        createdOrders.add(order);
    }

    static List<List<String>> orderColorsProvider() {
        return List.of(
                List.of(""),
                List.of("BLACK"),
                List.of("GREY"),
                List.of("BLACK", "GREY")
        );
    }
    @ParameterizedTest(name = "Создание заказа с цветом: {0}")
    @MethodSource("orderColorsProvider")
    @DisplayName("Можно указать один из цветов — BLACK или GREY; Можно указать оба цвета; Можно совсем не указывать цвет;")
    public void createOrderColorsTest(List<String> colors) {
        Allure.step("LOG createOrderColorsTest with colors: " + colors);

        Order order = new Order(
                "Иван" + UUID.randomUUID(),
                "Иванов",
                "ул. Пушкина, д. 10",
                "Черкизовская",
                "+7 999 111 22 33",
                5,
                "2026-11-25",
                "Позвонить за час до доставки",
                colors
        );

        createOrder(order);
    }

    @Test
    @DisplayName("Список заказов; В тело ответа возвращается список заказов.;")
    public void orderListTest(){

        Order order1 = new Order(
                "Иван"+ UUID.randomUUID(),
                "Иванов",
                "ул. Пушкина, д. 10",
                "Черкизовская",
                "+7 999 111 22 33",
                5,
                "2026-11-25",
                "Позвонить за час до доставки",
                List.of("BLACK")
        );
        Order order2 = new Order(
                "Петр" + UUID.randomUUID(),
                "Петров",
                "пр. Мира, д. 45, кв. 12",
                "ВДНХ",
                "+7 999 222 33 44",
                3,
                "2026-11-26",
                "Оставить у двери, если дома никого не будет",
                List.of("GREY")
        );
        Order order3 = new Order(
                "Анна" + UUID.randomUUID(),
                "Сидорова",
                "ул. Ленина, д. 8, стр. 2",
                "Сухаревская",
                "+7 999 333 44 55",
                7,
                "2026-11-27",
                "Код от домофона 2134",
                List.of("BLACK", "GREY")
        );

        createOrder(order1);
        createOrder(order2);
        createOrder(order3);

        Allure.step("Вызов GET /api/v1/orders");
            given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/orders")
                .then()
                .log().body()
                .statusCode(200);
    }
}
