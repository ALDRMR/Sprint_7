import apiClient.ApiCourierClient;
import model.Courier;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class CourierTests {

    private ApiCourierClient apiCourierClient;
    private List<Courier> createdCouriers;

    @BeforeEach
    public void setUp() {
        apiCourierClient = new ApiCourierClient();
        createdCouriers = new ArrayList<>();
    }

    @AfterEach
    public void TearDown() {
        System.out.println("TearDown createdCouriers count: " + createdCouriers.size());
        for (Courier courier : createdCouriers) {
            try {
                String id = getCourierId(courier);
                if (id != null) {
                    deleteCourier(id);
                }
            } catch (Exception e) {
                System.out.println("getId or deleteCourier Exception error: " + e.getMessage());
            }
        }
    }

    @Step("Создать курьера")
    public void createCourier(Courier courier) {

        apiCourierClient.createCourier(courier)
                .statusCode(201);

        createdCouriers.add(courier);
    }

    public String getCourierId(Courier courier) {
        Allure.step("Получить id курьера:" + courier.getLogin());
        Integer extractedId = apiCourierClient.loginCourier(courier)
                .statusCode(200)
                .extract()
                .path("id");

        courier.setId(extractedId.toString());

        return extractedId.toString();
    }

    public void deleteCourier(String id) {
        Allure.step("Удалить Курьера id: " + id);
        apiCourierClient.deleteCourier(id)
                .statusCode(200);
    }


    @Test
    @DisplayName("курьера можно создать; запрос возвращает правильный код ответа 201; успешный запрос возвращает ok: true;")
    public void createCourierTest() {
        Courier courier = new Courier(
                "courierLogin" + UUID.randomUUID(),
                "courier_password",
                "courier_name");

        Boolean answer = apiCourierClient.createCourier(courier)
                .statusCode(201)
                .extract()
                .path("ok");

        createdCouriers.add(courier);

        assertTrue(answer);
    }

    @Test
    @DisplayName("нельзя создать двух одинаковых курьеров; если создать пользователя с логином, который уже есть, возвращается ошибка.")
    public void couldNotCreateSameCourierTest(){
        Courier courier = new Courier(
                "courierLogin" + UUID.randomUUID(),
                "courier_password",
                "courier_name");

        createCourier(courier);

        String answer = apiCourierClient.createCourier(courier)
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
    public void notAllParametersCreateCourierTest(){
        Courier courier = new Courier(
                "courierLogin" + UUID.randomUUID(),
                "");

        String answer = apiCourierClient.createCourier(courier)
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

        Integer extractedId = apiCourierClient.loginCourier(courier)
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

        String answer = apiCourierClient.loginCourier(courierForLogin)
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

        String answer = apiCourierClient.loginCourier(courierForLogin)
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

        String answer = apiCourierClient.loginCourier(courier)
                .statusCode(404)
                .extract()
                .path("message");

        assertEquals(
                answer,
                "Учетная запись не найдена",
                "Текст сообщения не совпадает");
    }

}