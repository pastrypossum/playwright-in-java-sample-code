package com.serenitydojo.playwright.cucumber.pages;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import java.util.List;

public class ProductList {

    private final Page page;

    public ProductList(Page page) {
        this.page = page;
    }

    @Step("Get list of products displayed on this page")
    public List<ProductSummary> getDisplayed() {

        return page.locator(".card").all()
                .stream()
                .map(productCard -> {
                    String productName = productCard.getByTestId("product-name").textContent().strip();
                    String productPrice = price(productCard.getByTestId("product-price").textContent());
                    return new ProductSummary(productName, productPrice);
                }).toList();
    }

    public String getMessage() {

        return page.getByTestId("no-results").textContent();
    }

    private String price(String value) {
        return value.replace("$", "");
    }
}
