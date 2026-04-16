package com.serenitydojo.playwright.setup;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@UsePlaywright(MyOptions.class)
public class AnAnnotatedPlaywrightTest {

    private static final String BASE_URL = "https://practicesoftwaretesting.com/";

    @Test
    void shouldShowThePageTitle(Page page) {

        page.navigate(BASE_URL);
        assertThat(page.title()).contains("Practice Software Testing");
    }

    @Test
    void shouldSearchProductsByKeyword(Page page) {

        page.navigate(BASE_URL);
        page.locator("[placeholder=Search]").fill("Pliers");
        page.locator("button:has-text('Search')").click();

        int itemCount = page.locator(".card").count();
        assertThat(itemCount).isGreaterThan(0);
    }
}
