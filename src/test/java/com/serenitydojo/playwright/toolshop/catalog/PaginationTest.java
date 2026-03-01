package com.serenitydojo.playwright.toolshop.catalog;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.PaginationComponent;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.ProductList;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.SearchComponent;
import com.serenitydojo.playwright.toolshop.fixtures.ChromeHeadlessOptions;
import com.serenitydojo.playwright.toolshop.fixtures.TakesFinalScreenshot;
import com.serenitydojo.playwright.toolshop.fixtures.WithTracing;
import net.serenitybdd.annotations.Feature;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Story;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.playwright.junit5.SerenityPlaywrightExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SerenityJUnit5Extension.class)
@ExtendWith(SerenityPlaywrightExtension.class)
@UsePlaywright(ChromeHeadlessOptions.class)
@DisplayName("Product catalog pagination")
@Feature("Catalog")
public class PaginationTest implements TakesFinalScreenshot, WithTracing {

    @Steps
    PaginationComponent paginationComponent;

    @Steps
    ProductList productList;

    @Steps
    SearchComponent searchComponent;

    @BeforeEach
    void setUp(Page page) {
        PlaywrightSerenity.registerPage(page);
        page.navigate("https://practicesoftwaretesting.com");
        page.locator("nav ul.pagination").waitFor();
    }

    @Nested
    @DisplayName("When viewing the full product catalog")
    @Story("Viewing pagination controls")
    class WhenViewingTheFullProductCatalog {

        @Test
        @DisplayName("Should display pagination when there are more than 9 products")
        void shouldDisplayPagination() {
            assertThat(paginationComponent.isVisible()).isTrue();
        }

        @Test
        @DisplayName("Should show 9 products per page")
        void shouldShow9ProductsPerPage() {
            assertThat(productList.getProductNames()).hasSize(9);
        }

        @Test
        @DisplayName("Should start on page 1")
        void shouldStartOnPage1() {
            assertThat(paginationComponent.getActivePageNumber()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should disable the Previous button on the first page")
        void shouldDisablePreviousOnFirstPage() {
            assertThat(paginationComponent.isPreviousEnabled()).isFalse();
        }

        @Test
        @DisplayName("Should enable the Next button on the first page")
        void shouldEnableNextOnFirstPage() {
            assertThat(paginationComponent.isNextEnabled()).isTrue();
        }
    }

    @Nested
    @DisplayName("When navigating between pages")
    @Story("Navigating between pages")
    class WhenNavigatingBetweenPages {

        @Test
        @DisplayName("Should navigate to a specific page by clicking a page number")
        void shouldNavigateToSpecificPage() {
            paginationComponent.goToPage(2);

            assertThat(paginationComponent.getActivePageNumber()).isEqualTo(2);
            assertThat(productList.getProductNames()).hasSize(9);
        }

        @Test
        @DisplayName("Should navigate to the next page using the Next button")
        void shouldNavigateToNextPage() {
            paginationComponent.goToNextPage();

            assertThat(paginationComponent.getActivePageNumber()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should navigate to the previous page using the Previous button")
        void shouldNavigateToPreviousPage() {
            paginationComponent.goToPage(2);

            paginationComponent.goToPreviousPage();

            assertThat(paginationComponent.getActivePageNumber()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should display different products on each page")
        void shouldDisplayDifferentProductsOnEachPage() {
            var page1Products = productList.getProductNames();

            paginationComponent.goToPage(2);

            var page2Products = productList.getProductNames();

            assertThat(page1Products).doesNotContainAnyElementsOf(page2Products);
        }

        @Test
        @DisplayName("Should disable the Next button on the last page")
        void shouldDisableNextOnLastPage() {
            int lastPage = paginationComponent.getPageCount();
            paginationComponent.goToPage(lastPage);

            assertThat(paginationComponent.isNextEnabled()).isFalse();
            assertThat(paginationComponent.isPreviousEnabled()).isTrue();
        }
    }

    @Nested
    @DisplayName("When search results affect pagination")
    @Story("Search results and pagination")
    class WhenSearchResultsAffectPagination {

        @Test
        @DisplayName("Should hide pagination when search results fit on a single page")
        void shouldHidePaginationForFewResults() {
            searchComponent.searchBy("hammer");

            assertThat(paginationComponent.isVisible()).isFalse();
        }

        @Test
        @DisplayName("Should hide pagination when there are no search results")
        void shouldHidePaginationForNoResults() {
            searchComponent.searchBy("xyznonexistent");

            assertThat(paginationComponent.isVisible()).isFalse();
            assertThat(productList.getProductNames()).isEmpty();
        }

        @Test
        @DisplayName("Should restore pagination when clearing a search")
        void shouldRestorePaginationWhenClearingSearch() {
            searchComponent.searchBy("hammer");
            assertThat(paginationComponent.isVisible()).isFalse();

            searchComponent.clearSearch();

            assertThat(paginationComponent.isVisible()).isTrue();
        }
    }
}
