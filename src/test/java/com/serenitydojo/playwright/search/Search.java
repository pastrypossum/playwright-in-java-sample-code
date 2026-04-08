package com.serenitydojo.playwright.search;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;

public class Search {

    private final Page page;

    public Search(Page page) {
        this.page = page;
    }

    public void by(String keyword) {

        page.waitForResponse("**/products/search**", () -> {
            page.getByTestId("search-query").fill(keyword);
            page.getByTestId("search-submit").click();
        });
    }
}
