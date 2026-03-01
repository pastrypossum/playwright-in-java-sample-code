package com.serenitydojo.playwright.toolshop.catalog.workflow;

import com.microsoft.playwright.Page;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.*;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;

public class PurchaseWorkflow {

    Page page;
    @Steps NavBar navBar;
    @Steps SearchComponent searchComponent;
    @Steps ProductList productList;
    @Steps ProductDetails productDetails;
    @Steps ShoppingCart shoppingCart;
    @Steps AddressForm addressForm;
    @Steps PaymentForm paymentForm;

    public PurchaseWorkflow(Page page) {
        this.page = page;
    }

    @Step("Add {1} x '{0}' to cart")
    public void addProductToCart(String productName, int quantity) {
        navBar.openHomePage();
        searchComponent.searchBy(productName);
        productList.viewProductDetails(productName);
        productDetails.setQuantityTo(quantity);
        productDetails.addToCart();
    }

    @Step("Check out the cart")
    public void checkOutCart() {
        navBar.openCart();
        shoppingCart.proceedToCheckout();
    }

    @Step("Proceed to checkout after authentication")
    public void proceedToCheckoutAfterAuthentication() {
        shoppingCart.proceedToCheckoutAfterAuthentication();
    }

    @Step("Confirm address")
    public void confirmAddress() {
        addressForm.confirmAddress();
    }

    @Step("Choose payment method '{0}'")
    public void choosePaymentMethod(String paymentMethod) {
        paymentForm.choosePaymentMethod(paymentMethod);
    }

    public String confirmationMessage() {
        return page.locator("#order-confirmation").textContent();
    }
}
