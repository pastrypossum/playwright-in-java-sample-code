package com.serenitydojo.playwright.api;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@UsePlaywright
public class MockedAPITest {

    private static final String BASE_URL = "https://practicesoftwaretesting.com/";

    @DisplayName("Search for laser gun")
    @Test
    void shouldReturnLaserGun(Page page) {

        page.route("**/products/search?q=Laser Gun", route -> route.fulfill(
                new Route.FulfillOptions()
                        .setStatus(200)
                        .setContentType("application/json")
                        .setBody(MockAPIResponse.RESPONSE)
        ));

        page.navigate(BASE_URL);
        page.locator("[placeholder=Search]").fill("Laser Gun");
        page.locator("button:has-text('Search')").click();

        PlaywrightAssertions.assertThat(page.getByText("Laser Gun")).isVisible();
    }
}
