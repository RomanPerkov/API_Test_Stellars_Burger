import io.qameta.allure.Description;
import org.junit.Test;
import services.TestSteps;

import java.util.HashMap;
import java.util.Map;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

public class LoginUserTest extends BaseTest {

    final TestSteps testSteps = new TestSteps(restClientService);

    @Description("Тест проверяет залогиневание")
    @Test
    public void loginTest(){
        restClientService.validateResponse(testSteps.loginUser(fakeUser.get("email"), fakeUser.get("password"),LOGIN_ENDPOINT),200);
    }

    @Description("Тест проверяет залогиневание c неверным логином и паролем")
    @Test
    public void loginWrongLoginAndPasswordTest(){
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", faker.name().firstName());
        requestBody.put("password", faker.internet().password());
        restClientService.validateResponse(testSteps.loginUser((String) requestBody.get("email"),
                (String)requestBody.get("password"),LOGIN_ENDPOINT),SC_UNAUTHORIZED,"message",equalTo("email or password are incorrect"));
    }

}
