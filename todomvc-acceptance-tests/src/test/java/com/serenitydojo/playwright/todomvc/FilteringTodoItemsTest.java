package com.serenitydojo.playwright.todomvc;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.fixtures.ChromeHeadlessOptions;
import com.serenitydojo.playwright.todomvc.pageobjects.TodoMvcAppPage;
import io.qameta.allure.Feature;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@DisplayName("Filtering todo items")
@UsePlaywright(ChromeHeadlessOptions.class)
@Feature("Filtering todo items")
class FilteringTodoItemsTest {

    TodoMvcAppPage todoMvcApp;

    @BeforeEach
    void openApp(Page page) {
        todoMvcApp = new TodoMvcAppPage(page);
        todoMvcApp.open();
        todoMvcApp.addItems(Arrays.asList("Feed the cat", "Walk the dog", "Buy some milk"));
    }

    @DisplayName("All items should be displayed by default")
    @Test
    void allItemsShouldBeDisplayedByDefault() {
        // TODO: Implement me
        // 1) Add "Feed the cat", "Walk the dog", "Buy some milk"
        // 2) Verify that the active filter is set to "All"

        Assertions.assertThat(todoMvcApp.currentFilter()).isEqualTo("All");
    }

    @DisplayName("Should be able to filter active items")
    @Test
    void shouldBeAbleToFilterByActiveItems() {
        // TODO: Implement me
        // 1) Add "Feed the cat", "Walk the dog", "Buy some milk"
        // 2) Complete "Walk the dog"
        // 3) Apply the "Active" filter
        // 4) Verify that only "Feed the cat" and "Buy some milk" are displayed

        todoMvcApp.completeItem("Walk the dog");
        todoMvcApp.filterBy("Active");
        Assertions.assertThat(todoMvcApp.itemsDisplayed()).containsExactly("Feed the cat", "Buy some milk");
    }

    @DisplayName("Should be able to filter completed items")
    @Test
    void shouldBeAbleToFilterByCompletedItems() {
        // TODO: Implement me
        // 1) Add "Feed the cat", "Walk the dog", "Buy some milk"
        // 2) Complete "Walk the dog"
        // 3) Apply the "Completed" filter
        // 4) Verify that only "Walk the dog" is displayed

        todoMvcApp.completeItem("Walk the dog");
        todoMvcApp.filterBy("Completed");
        Assertions.assertThat(todoMvcApp.itemsDisplayed()).containsExactly("Walk the dog");
    }

    @DisplayName("Should be able to revert to showing all items")
    @Test
    void shouldBeAbleToRevertToShowingAllItems() {
        // TODO: Implement me
        // 1) Add "Feed the cat", "Walk the dog", "Buy some milk"
        // 2) Complete "Walk the dog"
        // 3) Apply the "Completed" filter
        // 4) Apply the "All" filter
        // 5) Verify that all three items ("Feed the cat", "Walk the dog", "Buy some milk") are displayed

        todoMvcApp.completeItem("Walk the dog");
        todoMvcApp.filterBy("Completed");
        todoMvcApp.filterBy("All");
        Assertions.assertThat(todoMvcApp.itemsDisplayed()).containsExactly("Feed the cat", "Walk the dog", "Buy some milk");
    }
}
