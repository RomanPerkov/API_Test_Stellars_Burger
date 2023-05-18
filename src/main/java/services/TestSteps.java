package services;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.http.Method;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class TestSteps {

    protected static final Faker faker = new Faker();

    private final RestClientService restClientService;

    public TestSteps(RestClientService restClientService) {
        this.restClientService = restClientService;
    }

    @Step("Получаем access токен")
    public String getAccessToken(Response response) {
        return response.jsonPath().getString("accessToken");
    }

    @Step("Залогиниваемся")
    public Response loginUser(String email, String password, String endpoint) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);
        return restClientService.sendRequestWithBody(Method.POST, endpoint, requestBody);
    }

    @Step("Генерируем пользователя")
    public static Map<String, String> generateUser() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", faker.internet().emailAddress());
        requestBody.put("password", faker.internet().password());
        requestBody.put("name", faker.name().firstName());
        return requestBody;
    }

    @Step("Регистрируем юзера")
    public Response registerUser(Map<String, String> fakeUser, String endpoint) {
        return restClientService.sendRequestWithBody(Method.POST, endpoint, fakeUser);
    }

    @Step("Удаляем юзера")
    public Response deleteUser(String accessToken, String endpoint) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", accessToken);
        return restClientService.sendRequestWithHeaders(Method.DELETE, endpoint, headers);
    }
}
