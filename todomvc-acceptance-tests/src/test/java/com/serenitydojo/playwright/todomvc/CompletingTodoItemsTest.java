package com.serenitydojo.playwright.todomvc;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.fixtures.ChromeHeadlessOptions;
import com.serenitydojo.playwright.todomvc.pageobjects.TodoMvcAppPage;
import io.qameta.allure.Feature;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

@DisplayName("Completing todo items to the list")
@UsePlaywright(ChromeHeadlessOptions.class)
@Feature("Completing todo items to the list")
class CompletingTodoItemsTest {

    TodoMvcAppPage todoMvcApp;

    @BeforeEach
    void openApp(Page page) {
        todoMvcApp = new TodoMvcAppPage(page);

        todoMvcApp.open();
        todoMvcApp.addItems(Arrays.asList("Feed the cat", "Walk the dog", "Buy some milk"));
    }

    @DisplayName("Completed items should be marked as completed")
    @Test
    void completedItemsShouldBeMarkedAsCompleted() {
        // TODO: Implement me
        // 1) Add "Feed the cat", "Walk the dog", "Buy some milk"
        // 2) Complete "Feed the cat"
        // 3) Check that "Feed the cat" is shown as completed

        todoMvcApp.completeItem("Feed the cat");
        Assertions.assertThat(todoMvcApp.completedItems()).contains("Feed the cat");
    }

    @DisplayName("Completing an item should update the number of items left count")
    @Test
    void shouldUpdateNumberOfItemsLeftCount() {
        // TODO: Implement me
        // 1) Add "Feed the cat", "Walk the dog", "Buy some milk"
        // 2) Complete "Feed the cat"
        // 3) Verify the todo count shows "2 items left!"

        todoMvcApp.completeItem("Feed the cat");
        Assertions.assertThat(todoMvcApp.itemLeftCount()).isEqualTo("2 items left!");
    }

    @DisplayName("Should be able to clear completed items")
    @Test
    void shouldBeAbleToClearCompletedItems() {
        // TODO: Implement me
        // 1) Add "Feed the cat", "Walk the dog", "Buy some milk"
        // 2) Complete "Walk the dog"
        // 3) Clear the completed items
        // 4) Verify that the remaining items are "Feed the cat" and "Buy some milk"

        todoMvcApp.completeItem("Walk the dog");
        todoMvcApp.clearCompletedItems();
        Assertions.assertThat(todoMvcApp.completedItems()).isEmpty();
    }
}
