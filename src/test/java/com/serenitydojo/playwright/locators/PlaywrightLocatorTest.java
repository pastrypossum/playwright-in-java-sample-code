package com.serenitydojo.playwright.locators;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.serenitydojo.playwright.PlaywrightTest;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class PlaywrightLocatorTest extends PlaywrightTest {

    @DisplayName("Locating elements by text")
    @Nested
    public class LocatingElementsByText {

        @DisplayName("Locating an element by text contents")
        @Test
        void byText() {
            page.getByText("Bolt Cutters").click();
            PlaywrightAssertions.assertThat(page.getByText("MightyCraft Hardware")).isVisible();
        }

        @DisplayName("Locating an element by alt text")
        @Test
        void byAltText() {
            page.getByAltText("Bolt Cutters").click();
            PlaywrightAssertions.assertThat(page.getByText("MightyCraft Hardware")).isVisible();
        }

        @DisplayName("Locating an element by title")
        @Test
        void byTitle() {
            page.getByAltText("Bolt Cutters").click();
            page.getByTitle("Practice Software Testing - Toolshop").click();

            assertThat(page.locator(".card").count()).isGreaterThan(0);
        }
    }

    @DisplayName("Locating elements by their label")
    @Nested
    public class LocatingElementsByLabel {

        @DisplayName("Locating an element by label")
        @Test
        void byLabelTest() {
            page.getByLabel("Search").fill("Pliers");
            page.locator("button:has-text('Search')").click();

            PlaywrightAssertions.assertThat(
                            page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Searched for:")))
                    .isVisible();

            assertThat(page.locator(".card").count()).isGreaterThan(0);
        }
    }

    @DisplayName("Locating elements by their placeholder")
    @Nested
    public class LocatingElementsByPlaceholder {

        @DisplayName("Locating an element by placeholder")
        @Test
        void byPlaceholderTest() {
            page.getByPlaceholder("Search").fill("Pliers");
            page.locator("button:has-text('Search')").click();

            PlaywrightAssertions.assertThat(
                            page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Searched for:")))
                    .isVisible();

            assertThat(page.locator(".card").count()).isGreaterThan(0);
        }
    }

    @DisplayName("Locating elements by their role")
    @Nested
    public class LocatingElementsByRole {

        @DisplayName("Locating an element by role")
        @Test
        void byButtonRoleTest() {
            page.getByPlaceholder("Search").fill("Pliers");

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search"))
                    .click();

            PlaywrightAssertions.assertThat(
                            page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions()
                                    .setName("Searched for:")))
                    .isVisible();

            PlaywrightAssertions.assertThat(
                            page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions()
                                    .setName("Searched for:")
                                    .setLevel(3)))
                    .isVisible();

            assertThat(page.locator(".card").count()).isGreaterThan(0);
        }
    }

    @DisplayName("Locating elements by test id")
    @Nested
    public class LocatingElementsByTestId {

        @DisplayName("Locating an element by test id")
        @Test
        void byLabelTestID() {
            page.getByTestId("search-query").fill("Pliers");
            page.getByTestId("search-submit").click();

            PlaywrightAssertions.assertThat(
                            page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Searched for:")))
                    .isVisible();

            assertThat(page.locator(".card").count()).isGreaterThan(0);
        }
    }

    @DisplayName("Locating elements by CSS")
    @Nested
    public class LocatingElementsByCSS {

        @BeforeEach
        public void setUp() {
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        @DisplayName("Locating elements by CSS ID")
        @Test
        void byCSS() {

            // # is short for ID
            page.locator("#first_name").fill("Robert");
            page.locator("input[data-test='last-name']").fill("Smith");

            // . is short for class
            page.locator(".btnSubmit").click();
            PlaywrightAssertions.assertThat(page.locator("#email_alert")).isVisible();

            List<String> alerts = page.locator(".alert").allTextContents();
            assertThat(alerts).size().isGreaterThan(0);
        }
    }

    @DisplayName("Filtering located items")
    @Nested
    public class FilteringItems {

        @DisplayName("Find all pliers")
        @Test
        void findAllPliers() {

            page.getByTestId("search-query").fill("Pliers");
            page.getByTestId("search-submit").click();

            PlaywrightAssertions.assertThat(page.locator(".card")).hasCount(4);
            List<String> allProductNames = page.getByTestId("product-name").allTextContents();
            assertThat(allProductNames).allMatch(name -> name.contains("Pliers"));
        }

        @DisplayName("Find out of stock pliers")
        @Test
        void findOutOfStockPliers() {

            page.getByTestId("search-query").fill("Pliers");
            page.getByTestId("search-submit").click();

            Locator outOfStock = page.locator(".card")
                    .filter(new Locator.FilterOptions().setHasText("Out of stock"))
                    .getByTestId("product-name");

            PlaywrightAssertions.assertThat(outOfStock).hasCount(1);
            PlaywrightAssertions.assertThat(outOfStock).hasText("Long Nose Pliers");
        }
    }
}