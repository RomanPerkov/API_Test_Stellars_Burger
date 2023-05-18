import io.qameta.allure.Description;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetOrderListTest extends BaseTest {

    protected static final String GET_ORDER_LIST = "/api/orders/all";
    protected static final String GET_USER_ORDER_LIST = "/api/orders";

    @Description("Тест проверяет получения списка заказов ")
    @Test
    public void getNoLoginOrderTest() {
        Response response = restClientService.sendRequest(Method.GET, GET_ORDER_LIST);
        restClientService.validateResponse(response, SC_OK);
    }

    @Description("Тест проверяет получения списка заказов конкретного пользователя ")
    @Test
    public void getLoginOrderTest() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", accessToken);
        Response response = restClientService.sendRequestWithHeaders(Method.GET, GET_USER_ORDER_LIST, headers);
        restClientService.validateResponse(response, SC_OK);
    }

    @Description("Тест проверяет получения списка заказов конкретного пользователя c неправильным токеном")
    @Test
    public void getLoginOrderWrongTokenTest() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "getAccessToken+1");
        Response response = restClientService.sendRequestWithHeaders(Method.GET, GET_USER_ORDER_LIST, headers);
        restClientService.validateResponse(response, SC_UNAUTHORIZED, "message", equalTo("You should be authorised"));
    }

}
