
import services.RestClientService;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import services.TestSteps;

import java.util.Map;

import static services.TestSteps.generateUser;


public class BaseTest {


    protected static final Faker faker = new Faker();
    final RestClientService restClientService = new RestClientService();
    final TestSteps testSteps = new TestSteps(restClientService);
    protected static final Map<String, String> fakeUser = generateUser();
    protected static final String LOGIN_ENDPOINT = "/api/auth/login";
    protected static final String REGISTER_ENDPOINT = "/api/auth/register";
    protected static final String AUTH_ENDPOINT = "/api/auth/user";
    protected static final String URL = "https://stellarburgers.nomoreparties.site";
    protected static String accessToken = "";

    @Before
    public void setUp() {
        RestAssured.baseURI = URL;
    }

    @Before
    public void registerUser() {
        Response response = testSteps.registerUser(fakeUser,REGISTER_ENDPOINT);
        accessToken = testSteps.getAccessToken(response);
    }

    @After
    public void deleteUser() {
        testSteps.deleteUser(accessToken,AUTH_ENDPOINT);
    }


}
