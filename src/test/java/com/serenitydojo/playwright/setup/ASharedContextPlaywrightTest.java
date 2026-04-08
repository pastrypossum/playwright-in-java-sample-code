package com.serenitydojo.playwright.setup;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class ASharedContextPlaywrightTest {

    private static final String BASE_URL = "https://practicesoftwaretesting.com/";

    private static Playwright playwright;
    private static Browser browser;
    private Page page;

    private static BrowserContext context;

    @BeforeAll
    public static void setUpClass() {

        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)
                        .setArgs(Arrays.asList("--no-sandbox","--start-maximized"))
        );
        context = browser.newContext();
    }

    @BeforeEach
    public void setUp() {
        page = context.newPage();
    }

    @AfterAll
    public static void tearDown() {
        browser.close();
        context.close();
        playwright.close();
    }

    @Test
    void shouldShowThePageTitle() {

        page.navigate(BASE_URL);
        assertThat(page.title()).contains("Practice Software Testing");
    }

    @Test
    void shouldSearchProductsByKeyword() {
        page.navigate(BASE_URL);
        page.locator("[placeholder=Search]").fill("Pliers");
        page.locator("button:has-text('Search')").click();

        int itemCount = page.locator(".card").count();
        assertThat(itemCount).isGreaterThan(0);
    }
}
