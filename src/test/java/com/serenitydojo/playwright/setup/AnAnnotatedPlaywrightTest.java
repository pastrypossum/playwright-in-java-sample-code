package com.serenitydojo.playwright.setup;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@UsePlaywright(AnAnnotatedPlaywrightTest.MyOptions.class)
public class AnAnnotatedPlaywrightTest {

    private static final String BASE_URL = "https://practicesoftwaretesting.com/";

    public static class MyOptions implements OptionsFactory {

        @Override
        public Options getOptions() {
            return new Options()
                    .setHeadless(true)
                    .setLaunchOptions(
                            new BrowserType.LaunchOptions()
                                    .setArgs(Arrays.asList("--no-sandbox","--disable-gpu"))
                    );
        }
    }

    // Can also pass browser, playawright or context as well as the page
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
