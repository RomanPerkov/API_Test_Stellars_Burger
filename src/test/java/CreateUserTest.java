import io.qameta.allure.Description;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import services.TestSteps;
import java.util.HashMap;
import java.util.Map;

import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.Matchers.equalTo;
import static services.TestSteps.generateUser;

public class CreateUserTest extends BaseTest {

    final TestSteps testSteps = new TestSteps(restClientService);

    private static String duplicateCourierId = null;

    @Description("Тест проверяет попытку создания дупликата курьера")
    @Test
    public void createDuplicateCourierTest() {
        Response response = restClientService.sendRequestWithBody(Method.POST, REGISTER_ENDPOINT, fakeUser);
        if (response.getStatusCode() == 200) {
            duplicateCourierId = testSteps.getAccessToken(response);
        }
        restClientService.validateResponse(response, SC_FORBIDDEN, "message", equalTo("User already exists"));
    }

    @Description("Тест проверяет попытку создание курьера без пароля")
    @Test
    public void createCourierNoPasswordTest() {
        Map<String, Object> missLogin = new HashMap<>(generateUser());
        missLogin.remove("email");
        Response response = restClientService.sendRequestWithBody(Method.POST, REGISTER_ENDPOINT, missLogin);
        restClientService.validateResponse(response, SC_FORBIDDEN, "message", equalTo("Email, password and name are required fields"));
    }


    @Description("Метод удаляет дупликат курьера, если таковой появится")
    @After
    public void deleteDuplicateCourier() {
        if (duplicateCourierId != null) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", duplicateCourierId);
            Response response = restClientService.sendRequestWithHeaders(Method.DELETE, AUTH_ENDPOINT, headers);
            restClientService.validateResponse(response, SC_ACCEPTED);
            duplicateCourierId = null;
        }
    }


}
