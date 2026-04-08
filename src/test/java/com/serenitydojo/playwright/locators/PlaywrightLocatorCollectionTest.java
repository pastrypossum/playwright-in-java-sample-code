package com.serenitydojo.playwright.locators;

import com.serenitydojo.playwright.PlaywrightTestMultiThread;
import com.serenitydojo.playwright.PlaywrightTestSingleThread;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PlaywrightLocatorCollectionTest extends PlaywrightTestMultiThread {

    @DisplayName("Using collections of items")
    @Nested
    public class UsingCollectionsOfItems {

        @DisplayName("Using a collection of items")
        @Test
        void usingCollectionOfItems() {
            page.getByTestId("search-query").fill("Pliers");
            page.getByTestId("search-submit").click();

//            page.locator(".card").first().click();
//            page.locator(".card").nth(3).click();
//            page.locator(".card").last().click();

            List<String> allText = page.getByTestId("product-name").allTextContents();
            allText.stream().forEach(e -> assertThat(e).contains("Pliers"));
        }
    }
}
