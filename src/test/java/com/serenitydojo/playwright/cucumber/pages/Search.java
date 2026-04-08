package com.serenitydojo.playwright.cucumber.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class Search {

    private final Page page;

    public Search(Page page) {
        this.page = page;
    }

    @Step("Search product list by keyword: {keyword}")
    public void byKeyword(String keyword) {

        page.waitForResponse("**/products/search**", () -> {
            page.getByTestId("search-query").fill(keyword);
            page.getByTestId("search-submit").click();
        });
    }

    @Step("Search product list by category: {category}")
    public void byCategory(String category) {

        page.waitForResponse("**/products?**&by_category=**", () -> {
            page.getByText(category).click();
        });
    }

    @Step("Sort product list by: {sort}")
    public void sortBy(String sort) {

        page.waitForResponse("**/products?**sort=**", () -> {
            page.getByTestId("sort").selectOption(sort);
        });
    }
}
