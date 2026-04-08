package com.serenitydojo.playwright.search;

import com.microsoft.playwright.Page;

import java.util.List;

public class Cart {

    private final Page page;

    public Cart(Page page) {
        this.page = page;
    }

    public void viewCart() {

        page.getByTestId("nav-cart").click();
        page.waitForSelector("app-cart tbody tr");
    }

    public List<CartListItem> getLineItems() {

        System.out.println("All cart items: " + page.locator("app-cart tbody tr").all());

        return page.locator("app-cart tbody tr").all()
                .stream()
                .map(
                        row -> {
                            String title = trimmed(row.getByTestId("product-title").innerText());
                            Integer quantity = Integer.parseInt(row.getByTestId("product-quantity").inputValue());
                            Double price = Double.parseDouble(price(row.getByTestId("product-price").innerText()));
                            Double total = Double.parseDouble(price(row.getByTestId("line-price").innerText()));
                            return  new CartListItem(title, quantity, price, total);
                        }
                ).toList();
    }

    private String price(String value) {
        return value.replace("$", "");
    }

    private String trimmed(String value) {
        return value.strip().replaceAll("\u00A0", "");
    }
}
