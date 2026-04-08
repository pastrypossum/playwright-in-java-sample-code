package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;

public class PlaywrightTestMultiThread {

    private static final String BASE_URL_UI = "https://practicesoftwaretesting.com/";
    private static final String BASE_URL_API = "https://api.practicesoftwaretesting.com/";

    protected static ThreadLocal<Playwright> playwright = ThreadLocal.withInitial(() -> {
        Playwright playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
        return playwright;
    });

    protected static ThreadLocal<Browser> browser = ThreadLocal.withInitial(() ->
            playwright.get().chromium().launch(
                new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setArgs(Arrays.asList("--no-sandbox", "--start-maximized"))));

    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    public static void setUpClass() {}

    @BeforeEach
    public void beforeEachTest() {

        context = browser.get().newContext();
        page = context.newPage();
        page.navigate("https://practicesoftwaretesting.com/");
    }

    @AfterEach
    public void afterEachTest() {

            context.close();
    }

    @AfterAll
    public static void tearDown() {

            browser.get().close();
            browser.remove();

            playwright.get().close();
            playwright.remove();
    }
}
