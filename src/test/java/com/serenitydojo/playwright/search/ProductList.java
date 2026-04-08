package com.serenitydojo.playwright.search;

import com.microsoft.playwright.Page;

import java.util.List;

public class ProductList {

    private final Page page;

    public ProductList(Page page) {
        this.page = page;
    }

    public List<ProductListItem> getNames() {

        return page.getByTestId("product-name")
                .allInnerTexts()
                .stream()
                .map(ProductListItem::new)
                .toList();
    }
}
