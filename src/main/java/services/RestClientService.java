package services;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matcher;

import java.util.Collections;
import java.util.Map;

import static io.restassured.RestAssured.given;


public class RestClientService {

    @Step("Делает запрос с телом и параметрами: метод - {httpMethod}, URL - {handle}, тело - {requestBody}, параметры - {queryParams}, заголовки - {headers}")
    public Response sendRequestWithBodyQueryParamsAndHeaders(Method httpMethod, String handle, Object requestBody, Map<String, Object> queryParams, Map<String, String> headers) {
        RequestSpecification requestSpecification = given()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .queryParams(queryParams != null ? queryParams : Collections.emptyMap()) // в случае если параметры не нужны
                .headers(headers != null ? headers : Collections.emptyMap()); // в случае если заголовки не нужны

        if (requestBody != null) {
            requestSpecification.body(requestBody);
        }

        return requestSpecification
                .when()
                .request(httpMethod, handle);

    }

    @Step("Делает запрос с телом и заголовками: метод - {httpMethod}, URL - {handle}, тело - {requestBody}, заголовки - {headers}")
    public Response sendRequestWithBodyAndHeaders(Method httpMethod, String handle, Object requestBody, Map<String, String> headers) {
        return sendRequestWithBodyQueryParamsAndHeaders(httpMethod, handle, requestBody, null, headers);
    }

    @Step("Делает запрос с параметрами и заголовками: метод - {httpMethod}, URL - {handle}, параметры - {queryParams}, заголовки - {headers}")
    public Response sendRequestWithBodyQueryParamsAndHeaders(Method httpMethod, String handle, Map<String, Object> queryParams, Map<String, String> headers) {
        return sendRequestWithBodyQueryParamsAndHeaders(httpMethod, handle, null, queryParams, headers);
    }

    @Step("Делает запрос только с заголовками: метод - {httpMethod}, URL - {handle}, заголовки - {headers}")
    public Response sendRequestWithHeaders(Method httpMethod, String handle, Map<String, String> headers) {
        return sendRequestWithBodyQueryParamsAndHeaders(httpMethod, handle, null, null, headers);
    }

    @Step("Делает запрос только с заголовками: метод - {httpMethod}, URL - {handle}, заголовки - {headers}")
    public Response sendRequestWithQueryParams(Method httpMethod, String handle, Map<String, Object> queryParams) {
        return sendRequestWithBodyQueryParamsAndHeaders(httpMethod, handle, null, queryParams, null);
    }

    @Step("Делает запрос с телом: метод - {httpMethod}, URL - {handle}, тело - {requestBody}")
    public Response sendRequestWithBody(Method httpMethod, String handle, Object requestBody) {
        return sendRequestWithBodyQueryParamsAndHeaders(httpMethod, handle, requestBody, null, null);
    }

    @Step("Делает запрос без тела: метод - {httpMethod}, URL - {handle}")
    public Response sendRequest(Method httpMethod, String handle) {
        return sendRequestWithBody(httpMethod, handle, null);
    }

    @Step("Проверяет код -{expectedStatusCode} ответа ")
    public ValidatableResponse validateResponse(Response response, int expectedStatusCode) {
        return validateResponse(response, expectedStatusCode, null, null);
    }

    @Step("Проверяет - {matcher} тело - {bodyCheck} ответа  ")
    public <T>ValidatableResponse validateResponse(Response response, String bodyCheck, Matcher<T> matcher) {
        return validateResponse(response, -1, bodyCheck, matcher);
    }

    @Step("Проверяет тело- {bodyCheck} и код {expectedStatusCode} ответа")
    public <T>ValidatableResponse validateResponse(Response response, int expectedStatusCode, String bodyCheck, Matcher<T> matcher) {
        ValidatableResponse responseValidatable;
        try {
            responseValidatable = response
                    .then().log()
                    .ifValidationFails();

            if (expectedStatusCode != -1) {
                responseValidatable.statusCode(expectedStatusCode);
            }

            if (bodyCheck != null && matcher != null) {
                responseValidatable.body(bodyCheck, matcher);
            }
        } catch (AssertionError error) {
            logValidationError(error, response);
            throw error;

        }

        return responseValidatable;
    }

    @Step("Логирование ошибок")
    public void logValidationError(Throwable error, Response response) {
        Allure.addAttachment("Ошибка", error.getMessage());
        // Код состояния
        int statusCode = response.getStatusCode();
        Allure.addAttachment("Код состояния", String.valueOf(statusCode));
// Заголовки ответа
        StringBuilder headers = new StringBuilder();
        response.getHeaders().forEach(header -> {
            headers.append(header.getName()).append(": ").append(header.getValue()).append("\n");
        });
        Allure.addAttachment("Заголовки ответа", headers.toString());
// Тело ответа
        String responseBody = response.getBody().asString();
        Allure.addAttachment("Тело ответа", responseBody);
    }
}
