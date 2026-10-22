import model.Courier;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;


public class CourierTests {

    private List<Courier> createdCouriers;

    @BeforeEach
    public void setUp() {
        baseURI = "https://qa-scooter.education-services.ru/";
        createdCouriers = new ArrayList<>();
    }

    @Step("Создать курьера POST /api/v1/courier")
    public void createCourier(Courier courier) {
        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .log().body()
                .statusCode(201);

        createdCouriers.add(courier);
    }

    public String getCourierId(Courier courier) {
        Allure.step("getCourierId " + "Вызов /api/v1/courier/login " + courier.getLogin());
        Integer extractedId = given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .log().body()
                .statusCode(200)
                .extract()
                .path("id");

        courier.setId(extractedId.toString());

        return extractedId.toString();
    }

    public void deleteCourier(String id) {
        Allure.step("Удалить Курьера, вызов /api/v1/courier/{id} id:" + id);
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .when()
                .delete("/api/v1/courier/{id}")
                .then()
                .log().body()
                .statusCode(200);
    }

    @AfterEach
    public void TearDown() {
        System.out.println("LOG TearDown" + " createdCouriers count: " + createdCouriers.size());
        for (Courier courier : createdCouriers) {
            try {
                String id = getCourierId(courier);
                if (id != null) {
                    deleteCourier(id);
                }
            } catch (Exception e) {
                System.out.println("getId or DeleteCourier error: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("курьера можно создать; запрос возвращает правильный код ответа 201; успешный запрос возвращает ok: true;")
    public void createCourierTest() {
        Courier courier = new Courier(
                "courierLogin" + UUID.randomUUID(),
                "courier_password",
                "courier_name");

        Allure.step("Вызов POST /api/v1/courier");
        Boolean answer = given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .log().body()
                .statusCode(201)
                .extract()
                .path("ok");

        createdCouriers.add(courier);

        assertTrue(answer);
    }

    @Disabled
    @Test
    @DisplayName("нельзя создать двух одинаковых курьеров; если создать пользователя с логином, который уже есть, возвращается ошибка.")
    public void couldNotCreateSameCourierTest(){
        Courier courier = new Courier(
                "courierLogin" + UUID.randomUUID(),
                "courier_password",
                "courier_name");

        createCourier(courier);

        Allure.step("Вызов POST /api/v1/courier");
        String answer = given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .log().body()
                .statusCode(409)
                .extract()
                .path("message");

        assertEquals(
                answer,
                "Этот логин уже используется", //"Этот логин уже используется. Попробуйте другой."
                "Текст сообщения не совпадает");
    }

    @Test
    @DisplayName("чтобы создать курьера, нужно передать в ручку все обязательные поля; если одного из полей нет, запрос возвращает ошибку;")
    public void NotAllParametersCreateCourierTest(){
        Courier courier = new Courier(
                "courierLogin" + UUID.randomUUID(),
                "");

        Allure.step("Вызов POST /api/v1/courier");
        String answer = given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .log().body()
                .statusCode(400)
                .extract()
                .path("message");

        assertEquals(
                answer,
                "Недостаточно данных для создания учетной записи",
                "Текст сообщения не совпадает");
    }

    @Test
    @DisplayName("Логин курьера; курьер может авторизоваться; для авторизации нужно передать все обязательные поля; успешный запрос возвращает id.")
    public void LoginCourierTest(){
        Courier courier = new Courier(
                "courierLogin" + UUID.randomUUID(),
                "courier_password",
                "courier_name");

        createCourier(courier);

        Allure.step("Вызов POST /api/v1/courier/login");
        Integer extractedId = given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .log().body()
                .statusCode(200)
                .extract()
                .path("id");

        courier.setId(extractedId.toString());
        assertNotNull(extractedId, "Id должен вернуться при положительном результате");
    }

    @Test
    @DisplayName("система вернёт ошибку, если неправильно указать логин или пароль;")
    public void LoginCourierWrongParameterTest(){
        Courier courier = new Courier(
                "courierLogin" + UUID.randomUUID(),
                "courier_password",
                "courier_name");

        createCourier(courier);

        Courier courierForLogin = new Courier(
                "courierLogin" + UUID.randomUUID(),
                "wrong_courier_password",
                "courier_name");

        Allure.step("Вызов POST /api/v1/courier/login");
        String answer = given()
                .contentType(ContentType.JSON)
                .body(courierForLogin)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .log().body()
                .statusCode(404)
                .extract()
                .path("message");

        assertEquals(
                answer,
                "Учетная запись не найдена",
                "Текст сообщения не совпадает");
    }

    @Test
    @DisplayName("если какого-то поля нет, запрос возвращает ошибку;")
    public void LoginCourierEmptyParameterTest(){
        String courierLogin = "courierLogin" + UUID.randomUUID();

        Courier courier = new Courier(
                courierLogin,
                "courier_password",
                "courier_name");

        createCourier(courier);

        Courier courierForLogin = new Courier(
                courierLogin,
                "",
                "courier_name");

        Allure.step("Вызов POST /api/v1/courier/login");
        String answer = given()
                .contentType(ContentType.JSON)
                .body(courierForLogin)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .log().body()
                .statusCode(400)
                .extract()
                .path("message");

        assertEquals(
                answer,
                "Недостаточно данных для входа",
                "Текст сообщения не совпадает");
    }

    @Test
    @DisplayName("если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;")
    public void LoginCourierNonExistentCourierTest(){
        Courier courier = new Courier(
                "courierLogin" + UUID.randomUUID(),
                "courier_password",
                "courier_name");

        Allure.step("Вызов POST /api/v1/courier/login");
        String answer = given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .log().body()
                .statusCode(404)
                .extract()
                .path("message");

        assertEquals(
                answer,
                "Учетная запись не найдена",
                "Текст сообщения не совпадает");
    }

}