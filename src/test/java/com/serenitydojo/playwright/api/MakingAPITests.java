package com.serenitydojo.playwright.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.RequestOptions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@UsePlaywright
public class MakingAPITests {

    private static final String BASE_URL_UI = "https://practicesoftwaretesting.com/";
    private static final String BASE_URL_API = "https://api.practicesoftwaretesting.com/";

    record Product(String name, Double price) {
    }

    private static APIRequestContext apiRequestContext;
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private Page page;

    @BeforeAll
    public static void setUpClass() {

        playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox", "--start-maximized"))
        );
        context = browser.newContext();
        apiRequestContext = playwright.request().newContext(
                new APIRequest.NewContextOptions().setBaseURL(BASE_URL_API)
                        .setExtraHTTPHeaders(new HashMap<>() {{
                            put("Accept", "application/json");
                        }})
        );
    }

    // milestone assertion
    // using json object because it is complex response
    // using gson to interact with json response
    static Stream<Product> products() {
        APIResponse response = apiRequestContext.get("/products?page=2");
        assertThat(response.status()).isEqualTo(200);

        JsonObject jsonObject = new Gson().fromJson(response.text(), JsonObject.class);
        JsonArray jsonArray = jsonObject.getAsJsonArray("data");

        return jsonArray.asList().stream()
                .map(jsonElement -> {
                    JsonObject product = jsonElement.getAsJsonObject();
                    return new Product(product.get("name").getAsString(), product.get("price").getAsDouble());
                });
    }

    @BeforeEach
    public void setUp() {
        page = context.newPage();
        page.navigate(BASE_URL_UI);
    }

    @AfterAll
    public static void tearDown() {

        if(apiRequestContext != null) {
            apiRequestContext.dispose();
        }

        browser.close();
        context.close();
        playwright.close();
    }


    @DisplayName("Check for known products")
    @ParameterizedTest(name = "Checking product {0}")
    @MethodSource("products")
    void shouldReturnBetweenPriceRange(Product product) {

        System.out.println("product: " + product.toString());

        page.getByLabel("Search").fill(product.name);
        page.locator("button:has-text('Search')").click();

        Locator productCard = page.locator(".card")
                .filter(new Locator.FilterOptions()
                        .setHasText(product.name)
                        .setHasText(Double.toString(product.price)));

        PlaywrightAssertions.assertThat(productCard).isVisible();

//        Locator searchResultProduct = page.locator(".card")
//                .filter(new Locator.FilterOptions().setHasText(product.name))
//                .getByTestId("product-price");
//
//        Double searchResultPrice = Double.valueOf(searchResultProduct.textContent().replace("$", ""));
//        assertThat(searchResultPrice).isEqualTo(product.price);
    }

    @DisplayName("Register User")
    @Test
    void shouldRegisterUser() {

        User expectedUser = User.randomUser();
        System.out.println("user: " + expectedUser.toString());

        APIResponse response = apiRequestContext.post("/users/register",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(expectedUser));

        System.out.println("APIResponse: " + response.statusText() + " " + response.text());
        assertThat(response.status()).isEqualTo(201);

        Gson gson = new Gson();
        User createdUser = gson.fromJson(response.text(), User.class);
        assertThat(createdUser).isEqualTo(expectedUser.withPassword(null));

        JsonObject responseObject = gson.fromJson(response.text(), JsonObject.class);

        SoftAssertions.assertSoftly(softly -> {

            softly.assertThat(response.status()).as("Return 201 code on successful user creation")
                    .isEqualTo(201);

            softly.assertThat(response.headers().get("content-type")).as("Return json content type")
                    .isEqualTo("application/json");

            softly.assertThat(responseObject.get("id").getAsString()).as("Return id with new user")
                    .isNotEmpty();

            softly.assertThat(responseObject.has("password")).as("Return no password with new user")
                    .isFalse();

            softly.assertThat(createdUser.first_name()).isEqualTo(expectedUser.first_name());
            softly.assertThat(createdUser.last_name()).isEqualTo(expectedUser.last_name());
            softly.assertThat(createdUser.email()).isEqualTo(expectedUser.email());
        });
    }

    @DisplayName("Missing email address")
    @Test
    void shouldThrowErrorOnMissingEmailAddress() {

        User testUser = new User (
                "Bob",
                "Rob",
                new User.Address(
                        "123 Main St",
                        "Anytown",
                        "Anystate",
                        "Anycountry",
                        "12345"
                ),
                "123-456-7890",
                "1990-01-01",
                null,
                "Q1@w2E3r"
        );

        APIResponse response = apiRequestContext.post("/users/register",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(testUser));

        Gson gson = new Gson();
        JsonObject responseObject = gson.fromJson(response.text(), JsonObject.class);

        System.out.println("APIResponse: " + response.statusText() + " " + response.text());
        assertThat(response.status()).isEqualTo(422);
        assertThat(response.statusText()).isEqualTo("Unprocessable Content");
        assertThat(responseObject.toString()).isEqualTo("{\"email\":[\"The email field is required.\"]}");
    }

    @DisplayName("Sign in as known user ")
    @Test
    void shouldSignIn() {

        User testUser = User.randomUser();

        APIResponse response = apiRequestContext.post("/users/register",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(testUser));

        System.out.println("APIResponse: " + response.statusText() + " " + response.text());

        page.navigate(BASE_URL_UI);
        page.getByText("Sign in").click();
        page.getByPlaceholder("Your email").fill(testUser.email());
        page.getByPlaceholder("Your password").fill(testUser.password());
        page.getByTestId("login-submit").click();

        PlaywrightAssertions.assertThat(page.getByTestId("page-title")).containsText("My account");
        PlaywrightAssertions.assertThat(page.getByText("Here you can manage your profile, favorites and orders.")).isVisible();
    }

    @DisplayName("Reject with incorrect password")
    @Test
    void shouldRejectWithIncorrectPassword() {

        User testUser = User.randomUser();

        APIResponse response = apiRequestContext.post("/users/register",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(testUser));

        System.out.println("APIResponse: " + response.statusText() + " " + response.text());

        page.navigate(BASE_URL_UI);
        page.getByText("Sign in").click();
        page.getByPlaceholder("Your email").fill(testUser.email());
        page.getByPlaceholder("Your password").fill("sdadaer23_12AB");
        page.getByTestId("login-submit").click();

        PlaywrightAssertions.assertThat(page.getByTestId("login-error"))
                .containsText("Invalid email or password");
    }

    }
