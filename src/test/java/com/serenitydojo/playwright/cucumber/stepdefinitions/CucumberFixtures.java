package com.serenitydojo.playwright.cucumber.stepdefinitions;

import com.microsoft.playwright.*;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;

import java.util.Arrays;

public class CucumberFixtures {

    private static ThreadLocal<Playwright> playwright = ThreadLocal.withInitial(() -> {
        Playwright playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
        return playwright;
    });

    private static ThreadLocal<Browser> browser = ThreadLocal.withInitial(() ->
            playwright.get().chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(true)
                            .setArgs(Arrays.asList("--no-sandbox", "--start-maximized"))));

    private static ThreadLocal<BrowserContext> context = new ThreadLocal<>();

    private static ThreadLocal<Page> page = new ThreadLocal<>();

    @Before (order = 1)
    public void beforeEachTest() {

        context.set(browser.get().newContext());
        page.set(context.get().newPage());
    }

    @After
    public void afterEachTest() {

        context.get().close();
        context.remove();
    }

    @AfterAll
    public static void tearDown() {

        browser.get().close();
        browser.remove();

        playwright.get().close();
        playwright.remove();
    }

    public static Page getPage(){

        return page.get();
    }

    public static BrowserContext getContext(){

        return context.get();
    }
}
