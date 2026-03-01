package com.serenitydojo.playwright.toolshop.purchase;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.toolshop.catalog.workflow.AuthenticationWorkflow;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.*;
import com.serenitydojo.playwright.toolshop.catalog.workflow.PurchaseWorkflow;
import com.serenitydojo.playwright.toolshop.domain.User;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@ExtendWith(SerenityJUnit5Extension.class)
@ExtendWith(SerenityPlaywrightExtension.class)
@UsePlaywright(ChromeHeadlessOptions.class)
@DisplayName("Purchase")
@Feature("Purchases")
public class PurchaseTest implements TakesFinalScreenshot, WithTracing {

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

    @Steps
    AuthenticationWorkflow authentication;

    @Steps
    PurchaseWorkflow purchases;

    @BeforeEach
    void openHomePage() {
        navBar.openHomePage();
    }

    @BeforeEach
    void setUp(Page page) {
        PlaywrightSerenity.registerPage(page);
    }

    @Story("Purchase items")
    @Nested
    class WhenPurchasingItems {

        @Test
        @DisplayName("Purchasing a number of items after logging on")
        void whenPurchasingANumberOfItemsAfterLoggingOn() {
            // Given Sharon has an account
            User sharon = authentication.registerUserCalled("Sharon");
            // And Sharon has logged on
            authentication.loginAs(sharon);

            // When she adds 2 items to the cart
            purchases.addProductToCart("Bolt Cutters", 2);
            purchases.addProductToCart("Slip Joint Pliers", 1);

            // And she checks out
            purchases.checkOutCart();
            purchases.proceedToCheckoutAfterAuthentication();
            purchases.confirmAddress();

            // And she completes the purchase
            purchases.choosePaymentMethod("Cash on Delivery");

            // Then she should receive a thank you message
            Assertions.assertThat(purchases.confirmationMessage()).contains("Thanks for your order!");
        }

        @Test
        @DisplayName("Logging on during the purchase process")
        void whenLoggingOnDuringThePurchaseProcess() {
            // Given Sharon has an account
            User sharon = authentication.registerUserCalled("Sharon");

            // When she adds 2 items to the cart
            purchases.addProductToCart("Bolt Cutters", 2);
            purchases.addProductToCart("Slip Joint Pliers", 1);

            // And she checks out
            purchases.checkOutCart();

            // And she logs on
            authentication.loginAs(sharon);
            purchases.checkOutCart(); // again

            purchases.proceedToCheckoutAfterAuthentication();
            purchases.confirmAddress();

            // And she completes the purchase
            purchases.choosePaymentMethod("Cash on Delivery");

            // Then she should receive a thank you message
            Assertions.assertThat(purchases.confirmationMessage()).contains("Thanks for your order!");
        }

    }
}