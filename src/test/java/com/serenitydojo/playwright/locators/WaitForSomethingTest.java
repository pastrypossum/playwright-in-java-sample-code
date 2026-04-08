package com.serenitydojo.playwright.locators;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.serenitydojo.playwright.PlaywrightTestMultiThread;
import com.serenitydojo.playwright.PlaywrightTestSingleThread;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WaitForSomethingTest extends PlaywrightTestMultiThread {

    @DisplayName("Check for loaded product list")
    @Nested
    public class LoadedProductList {

        @BeforeEach
        public void setUp() {
            page.navigate("https://practicesoftwaretesting.com/");
            page.waitForSelector(".card-img-top");
        }

        @DisplayName("Display product names")
        @Test
        void shouldDisplayProductNames() {

            List<String> productNames = page.getByTestId("product-name").allInnerTexts();
            assertThat(productNames).contains("Bolt Cutters", "Claw Hammer", "Pliers");
        }

        @DisplayName("Display product images")
        @Test
        void shouldDisplayProductImages() {

            List<String> productImages = page.locator(".card-img-top").all()
                    .stream().map(img -> img.getAttribute("alt")).toList();

            assertThat(productImages).contains("Bolt Cutters", "Claw Hammer", "Pliers");
        }
    }

    @DisplayName("Filter by category")
    @Nested
    public class FilterByCategory {

        @BeforeEach
        public void setUp() {
            page.navigate("https://practicesoftwaretesting.com/");
            page.waitForSelector(".card-img-top");
        }

        @DisplayName("Filter by category")
        @Test
        void shouldFilerByCategory() {
            page.getByRole(AriaRole.MENUBAR).getByText("Categories").click();
            page.getByRole(AriaRole.MENUBAR).getByText("Power Tools").click();

            // Add some specific controls on wait for selector
            page.waitForSelector(".card-img-top",
                    new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));

            List<String> productNames = page.getByTestId("product-name").allInnerTexts();
            assertThat(productNames).contains("Belt Sander", "Cordless Drill 20V", "Cordless Drill 12V");
        }
    }

    @DisplayName("Add item to cart")
    @Nested
    public class AddItemToCart {

        @BeforeEach
        public void setUp() {
            page.navigate("https://practicesoftwaretesting.com/");
            page.waitForSelector(".card-img-top");
        }

        @DisplayName("Add item to cart")
        @Test
        void shouldAddItemToCart() {

            page.getByText("Combination Pliers").click();
            page.getByText("Add to cart").click();

            PlaywrightAssertions.assertThat(page.getByRole(AriaRole.ALERT)).isVisible();
            PlaywrightAssertions.assertThat(page.getByRole(AriaRole.ALERT)).containsText("Product added to shopping cart");

            page.waitForCondition( () -> page.getByRole(AriaRole.ALERT).isHidden());
        }

        @DisplayName("Update cart item count")
        @Test
        void shouldUpdateCartItemCount() {

            page.getByText("Combination Pliers").click();
            page.getByText("Add to cart").click();

            page.waitForCondition( () -> page.getByTestId("cart-quantity").textContent().equals("1"));
        }

        @DisplayName("Update sort order")
        @Test
        void shouldUpdateSortOrder() {

            page.waitForResponse("**/products?page=0&sort=price,desc**",
                    () -> page.getByTestId("sort").selectOption("Price (High - Low)") );

            var productPrices = page.getByTestId("product-price").allInnerTexts().stream()
                    .map(price -> Double.parseDouble(price.replace("$", "")))
                    .toList();

            System.out.println(productPrices);
            assertThat(productPrices).isNotEmpty().isSortedAccordingTo(Comparator.reverseOrder());
        }
    }
}
