package com.serenitydojo.playwright.toolshop.catalog;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.*;
import com.serenitydojo.playwright.toolshop.fixtures.ChromeHeadlessOptions;
import com.serenitydojo.playwright.toolshop.fixtures.TakesFinalScreenshot;
import com.serenitydojo.playwright.toolshop.fixtures.WithTracing;
import net.serenitybdd.annotations.Feature;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Story;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.playwright.junit5.SerenityPlaywrightExtension;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.data.Offset.offset;

@ExtendWith(SerenityJUnit5Extension.class)
@ExtendWith(SerenityPlaywrightExtension.class)
@UsePlaywright(ChromeHeadlessOptions.class)
@DisplayName("Shopping Cart")
@Feature("Shopping Cart")
public class AddToCartTest implements TakesFinalScreenshot, WithTracing {

    @Steps
    SearchComponent searchComponent;

    @Steps
    ProductList productList;

    @Steps
    ProductDetails productDetails;

    @Steps
    NavBar navBar;

    @Steps
    ShoppingCart shoppingCart;

    @BeforeEach
    void openHomePage() {
        navBar.openHomePage();
    }

    @BeforeEach
    void setUp(Page page) {
        PlaywrightSerenity.registerPage(page);
    }


    @Nested
    @Story("Checking out a product")
    class WhenCheckingOutAProduct {

        @Test
        @DisplayName("Checking out a single item")
        void whenCheckingOutASingleItem() {

            searchComponent.searchBy("pliers");
            productList.viewProductDetails("Slip Joint Pliers");

            productDetails.setQuantityTo(2);
            productDetails.addToCart();

            navBar.openCart();

            List<CartLineItem> lineItems = shoppingCart.getLineItems();

            Assertions.assertThat(lineItems)
                    .hasSize(1)
                    .first()
                    .satisfies(item -> {
                        Assertions.assertThat(item.title()).contains("Slip Joint Pliers");
                        Assertions.assertThat(item.quantity()).isEqualTo(3);
                        Assertions.assertThat(item.total()).isCloseTo(item.quantity() * item.price(), offset(0.01));
                    });
        }

        @Test
        @DisplayName("Checking out multiple items")
        void whenCheckingOutMultipleItems() {
            navBar.openHomePage();

            productList.viewProductDetails("Bolt Cutters");
            productDetails.setQuantityTo(2);
            productDetails.addToCart();

            navBar.openHomePage();
            productList.viewProductDetails("Slip Joint Pliers");
            productDetails.addToCart();

            navBar.openCart();

            List<CartLineItem> lineItems = shoppingCart.getLineItems();

            Assertions.assertThat(lineItems).hasSize(2);
            List<String> productNames = lineItems.stream().map(CartLineItem::title).toList();
            Assertions.assertThat(productNames).contains("Bolt Cutters", "Slip Joint Pliers");

            Assertions.assertThat(lineItems)
                    .allSatisfy(item -> {
                        Assertions.assertThat(item.quantity()).isGreaterThanOrEqualTo(1);
                        Assertions.assertThat(item.price()).isGreaterThan(0.0);
                        Assertions.assertThat(item.total()).isGreaterThan(0.0);
                        Assertions.assertThat(item.total()).isCloseTo(item.quantity() * item.price(), offset(0.01));
                    });

        }
    }
}