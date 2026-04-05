package com.serenitydojo.playwright.setup;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.*;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class ASimplePlaywrightTest {

    private static final String BASE_URL = "https://practicesoftwaretesting.com/";

    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeEach
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox","--start-maximized"))
        );
        page = browser.newPage();
    }

    @AfterEach
    public void tearDown() {
        browser.close();
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
