import io.qameta.allure.Description;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static services.TestSteps.generateUser;

public class ChangeUserDataTest extends BaseTest {


    @Description("Тест проверяет изменение данных пользователя с авторизацией")
    @Test
    public void updateUserDataWithAuthorizationTest() {
        // Создаем новые данные для пользователя
        Map<String, String> updatedUserData = generateUser();
        // Отправляем запрос на обновление данных с авторaизацией
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", accessToken);
        Response response = restClientService.sendRequestWithBodyAndHeaders(Method.PATCH, AUTH_ENDPOINT, updatedUserData, headers);
        // Проверяем статус код ответа и обновленные данные
        restClientService.validateResponse(response, SC_OK);
        restClientService.validateResponse(response, "success", equalTo(true));
        restClientService.validateResponse(response, "user.email", equalTo(updatedUserData.get("email")));
        restClientService.validateResponse(response, "user.name", equalTo(updatedUserData.get("name")));
    }

    @Description("Тест проверяет изменение данных пользователя без авторизации")
    @Test
    public void updateUserDataWithoutAuthorizationTest() {
        // Создаем новые данные для пользователя
        Map<String, String> updatedUserData = generateUser();
        // Отправляем запрос на обновление данных без авторизации
        Response response = restClientService.sendRequestWithBody(Method.PATCH, AUTH_ENDPOINT, updatedUserData);
        // Проверяем статус код ответа и сообщение об ошибке
        restClientService.validateResponse(response, SC_UNAUTHORIZED);
        restClientService.validateResponse(response, "success", equalTo(false));
        restClientService.validateResponse(response, "message", equalTo("You should be authorised"));
    }
}
