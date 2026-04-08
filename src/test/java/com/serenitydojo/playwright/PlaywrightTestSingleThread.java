package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;

public class PlaywrightTestSingleThread {

    private static final String BASE_URL_UI = "https://practicesoftwaretesting.com/";
    private static final String BASE_URL_API = "https://api.practicesoftwaretesting.com/";

    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext context;
    protected Page page;

    @BeforeAll
    public static void setUpClass() {
        playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox", "--start-maximized"))
        );
    }

    @BeforeEach
    public void beforeEachTest() {
        context = browser.newContext();
        page = context.newPage();
        page.navigate("https://practicesoftwaretesting.com/");
    }

    @AfterEach
    public void afterEachTest() {
        if(context != null) {
            context.close();
        }
    }

    @AfterAll
    public static void tearDown() {
        if(browser != null) {
            browser.close();
        }
        if(playwright != null) {
            playwright.close();
        }
    }
}
