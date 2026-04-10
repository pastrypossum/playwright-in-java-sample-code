package com.serenitydojo.playwright.todomvc.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class TodoMvcAppPage {

    private final Page page;
    private final String baseUrl;
    private final Locator totoItems;
    private final Locator totoField;

    public TodoMvcAppPage(Page page) {
        this.page = page;
        baseUrl = (StringUtils.isEmpty(System.getenv("APP_HOST_URL"))) ? "http://localhost:7002" : System.getenv("APP_HOST_URL");

        totoItems = page.getByTestId("todo-item");
        totoField = page.getByTestId("text-input");
    }

    public void open() {
        page.navigate(baseUrl);
    }

    // TODO: Add page object methods here

    public void isLoaded(){

        PlaywrightAssertions.assertThat(totoField).isVisible();
        PlaywrightAssertions.assertThat(totoField).hasAttribute("placeholder", "What needs to be done?");
    }

    public Locator todoField(){

        return totoField;
    }

    public List<String> itemsDisplayed(){

        return totoItems.allTextContents();
    }

    public void addItem(String expectedItem) {

        totoField.fill(expectedItem);
        totoField.press("Enter");
    }

    public void addItems(List<String> expectedItem) {

        expectedItem.forEach(this::addItem);
    }

    public void removeItem(String todoItem) {

        Locator row = findRow(todoItem);

        row.hover();
        row.getByTestId("todo-item-button").click();
    }

    public void completeItem(String todoItem) {

        Locator row = findRow(todoItem);
        row.getByTestId("todo-item-toggle").click();
    }

    public List<String> completedItems() {

        return page.locator(".completed").allInnerTexts();
    }

    public String itemLeftCount() {

        return page.locator(".todo-count").textContent();
    }

    public void clearCompletedItems() {

        page.locator(".clear-completed").click();
    }

    public void filterBy(String filter) {

        page.getByTestId("footer-navigation").getByText(filter).click();
    }

    public String currentFilter() {

        return page.locator(".selected").textContent();
    }

    private Locator findRow(String todoItem) {

        return page.getByTestId("todo-item").filter(new Locator.FilterOptions().setHasText(todoItem));
    }
}