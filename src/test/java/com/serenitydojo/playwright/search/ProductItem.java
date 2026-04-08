package com.serenitydojo.playwright.search;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;

public class ProductItem {

    private final Page page;

    public ProductItem(Page page) {
        this.page = page;
    }

    public void viewProductDetails(String item){

        page.getByText(item).click();
        PlaywrightAssertions.assertThat(page.getByTestId("product-name")).hasText(item);
    }

    public void setQuantity(Integer quantity) {

        page.getByTestId("quantity").fill(quantity.toString());
    }

    public void addToCart(){

        page.getByTestId("add-to-cart").click();

        page.waitForCondition( () -> page.getByRole(AriaRole.ALERT).isVisible());

        PlaywrightAssertions.assertThat(page.getByRole(AriaRole.ALERT))
                .containsText("Product added to shopping cart");

        page.waitForCondition( () -> page.getByRole(AriaRole.ALERT).isHidden());
    }
}
