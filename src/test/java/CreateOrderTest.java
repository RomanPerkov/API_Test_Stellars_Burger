import io.qameta.allure.Description;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.*;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;


public class CreateOrderTest extends BaseTest {

    protected static final String INGREDIENTS_ENDPOINT = "/api/ingredients";
    protected static final String ORDERS_ENDPOINT = "/api/orders";

    @Description("Тест проверяет создание заказа с авторизацией и с ингредиентами")
    @Test
    public void createOrderWithLoginAndIngredientsTest() {
        // Получаем список ингредиентов
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", accessToken);
        Response ingredientsResponse = restClientService.sendRequestWithHeaders(Method.GET, INGREDIENTS_ENDPOINT, headers);
        restClientService.validateResponse(ingredientsResponse, SC_OK);
        List<String> ingredientIds = ingredientsResponse.jsonPath().getList("data._id");
        // Создаем заказ с ингредиентами
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", ingredientIds);
        Response response = restClientService.sendRequestWithBodyAndHeaders(Method.POST, ORDERS_ENDPOINT, requestBody, headers);

        // Проверяем статус код ответа и успешность создания заказа
        restClientService.validateResponse(response, SC_OK, "success", equalTo(true));
    }

    @Description("Тест проверяет создание заказа без авторизации с индигридиентами ")
    @Test
    public void createOrderNoLoginAndWithIngredientsTest() {
        // Получаем список ингредиентов
        Response ingredientsResponse = restClientService.sendRequest(Method.GET, INGREDIENTS_ENDPOINT);
        restClientService.validateResponse(ingredientsResponse, SC_OK);
        List<String> ingredientIds = ingredientsResponse.jsonPath().getList("data._id");
        // Создаем заказ без авторизации
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", ingredientIds);
        Response response = restClientService.sendRequestWithBody(Method.POST, ORDERS_ENDPOINT, requestBody);
        // Проверяем статус код ответа и успешность создания заказа
        restClientService.validateResponse(response, SC_OK, "success", equalTo(true));
    }

    @Description("Тест проверяет создание заказа без ингредиентов без авторизации")
    @Test
    public void createOrderNoIngredientsAndLoginTest() {
        // Создаем заказ без ингредиентов
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", Collections.emptyList());
        Response response = restClientService.sendRequestWithBody(Method.POST, ORDERS_ENDPOINT, requestBody);
        // Проверяем статус код ответа и сообщение об ошибке
        restClientService.validateResponse(response, SC_BAD_REQUEST, "success", equalTo(false));
        restClientService.validateResponse(response, "message", equalTo("Ingredient ids must be provided"));
    }

    @Description("Тест проверяет создание заказа с неверным хешем ингредиентов")
    @Test
    public void createOrderWithInvalidIngredientHashTest() {
        // Создаем заказ с неверным хшем ингредиента
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", Arrays.asList("invalidIngredientId1", "invalidIngredientId2"));
        Response response = restClientService.sendRequestWithBody(Method.POST, ORDERS_ENDPOINT, requestBody);
        restClientService.validateResponse(response, SC_INTERNAL_SERVER_ERROR);
    }
}
