package com.serenitydojo.playwright.toolshop.catalog;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.ProductList;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.SearchComponent;
import com.serenitydojo.playwright.toolshop.fixtures.ChromeHeadlessOptions;
import com.serenitydojo.playwright.toolshop.fixtures.ProductSummary;
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

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SerenityJUnit5Extension.class)
@ExtendWith(SerenityPlaywrightExtension.class)
@UsePlaywright(ChromeHeadlessOptions.class)
@DisplayName("Filtering and sorting products")
@Feature("Catalog")
public class FilterAndSortProductsTest implements TakesFinalScreenshot, WithTracing {

    @Steps
    SearchComponent searchComponent;

    @Steps
    ProductList productList;

    @BeforeEach
    void setUp(Page page) {
        PlaywrightSerenity.registerPage(page);
        page.navigate("https://practicesoftwaretesting.com");
        page.locator("nav ul.pagination").waitFor();
    }

    @Nested
    @DisplayName("When filtering by category")
    @Story("Filtering by category")
    class WhenFilteringByCategory {

        @Test
        @DisplayName("Should show only products in the selected subcategory")
        void shouldShowProductsInSubcategory(Page page) {
            searchComponent.filterBy("Hand Saw");

            var products = productList.getProductNames();

            assertThat(products).contains("Wood Saw");
            assertThat(products).hasSizeLessThanOrEqualTo(9);
        }

        @Test
        @DisplayName("Should show products from a parent category")
        void shouldShowProductsFromParentCategory(Page page) {
            searchComponent.filterBy("Power Tools");

            var products = productList.getProductNames();

            assertThat(products).containsAnyOf("Circular Saw", "Cordless Drill 24V");
        }

        @Test
        @DisplayName("Should narrow results when combining search with a category filter")
        void shouldNarrowResultsWithSearchAndCategoryFilter(Page page) {
            searchComponent.searchBy("saw");
            searchComponent.filterBy("Hand Saw");

            var products = productList.getProductNames();

            assertThat(products).containsExactly("Wood Saw");
        }

        @Test
        @DisplayName("Should combine multiple subcategory filters")
        void shouldCombineMultipleSubcategoryFilters(Page page) {
            searchComponent.filterBy("Hammer");
            searchComponent.filterBy("Screwdriver");

            var products = productList.getProductNames();

            assertThat(products).anyMatch(name -> name.toLowerCase().contains("hammer"));
            assertThat(products).anyMatch(name -> name.toLowerCase().contains("screwdriver"));
        }
    }

    @Nested
    @DisplayName("When filtering by brand")
    @Story("Filtering by brand")
    class WhenFilteringByBrand {

        @Test
        @DisplayName("Should show only products from the selected brand")
        void shouldShowProductsFromSelectedBrand(Page page) {
            searchComponent.filterByBrand("MightyCraft Hardware");

            var filteredProducts = productList.getProductNames();

            assertThat(filteredProducts).isNotEmpty();
            assertThat(filteredProducts).contains("Claw Hammer");
        }

        @Test
        @DisplayName("Should combine brand and category filters")
        void shouldCombineBrandAndCategoryFilters(Page page) {
            searchComponent.filterByBrand("MightyCraft Hardware");
            searchComponent.filterBy("Pliers");

            var products = productList.getProductNames();

            assertThat(products).isNotEmpty();
            assertThat(products).contains("Long Nose Pliers", "Slip Joint Pliers", "Bolt Cutters");
            assertThat(products).doesNotContain("Claw Hammer with Shock Reduction Grip");
        }
    }

    @Nested
    @DisplayName("When sorting products")
    @Story("Sorting products")
    class WhenSortingProducts {

        @Test
        @DisplayName("Should sort products by name A to Z")
        void shouldSortByNameAscending(Page page) {
            searchComponent.sortBy("Name (A - Z)");

            var products = productList.getProductNames();

            assertThat(products).first().isEqualTo("Adjustable Wrench");
            assertThat(products).isSorted();
        }

        @Test
        @DisplayName("Should sort products by name Z to A")
        void shouldSortByNameDescending(Page page) {
            searchComponent.sortBy("Name (Z - A)");

            var products = productList.getProductNames();

            assertThat(products).first().isEqualTo("Wood Saw");
            assertThat(products).isSortedAccordingTo(Comparator.reverseOrder());
        }

        @Test
        @DisplayName("Should sort products by price low to high")
        void shouldSortByPriceLowToHigh(Page page) {
            searchComponent.sortBy("Price (Low - High)");

            var summaries = productList.getProductSummaries();

            assertThat(summaries.getFirst().name()).isEqualTo("Washers");
            assertThat(extractPrices(summaries)).isSorted();
        }

        @Test
        @DisplayName("Should sort products by price high to low")
        void shouldSortByPriceHighToLow(Page page) {
            searchComponent.sortBy("Price (High - Low)");

            var summaries = productList.getProductSummaries();

            assertThat(summaries.getFirst().name()).isEqualTo("Drawer Tool Cabinet");
            assertThat(extractPrices(summaries)).isSortedAccordingTo(Comparator.reverseOrder());
        }
    }

    @Nested
    @DisplayName("When combining sorting with filters")
    @Story("Sorting filtered results")
    class WhenCombiningSortingWithFilters {

        @Test
        @DisplayName("Should sort filtered results by name")
        void shouldSortFilteredResultsByName(Page page) {
            searchComponent.filterBy("Hammer");
            searchComponent.sortBy("Name (A - Z)");

            var products = productList.getProductNames();

            assertThat(products).isNotEmpty();
            assertThat(products).allMatch(name -> name.toLowerCase().contains("hammer"));
            assertThat(products).isSorted();
        }

        @Test
        @DisplayName("Should sort filtered results by price")
        void shouldSortFilteredResultsByPrice(Page page) {
            searchComponent.filterBy("Hammer");
            searchComponent.sortBy("Price (Low - High)");

            var summaries = productList.getProductSummaries();

            assertThat(summaries).isNotEmpty();
            assertThat(summaries).allMatch(s -> s.name().toLowerCase().contains("hammer"));
            assertThat(extractPrices(summaries)).isSorted();
        }
    }

    private List<Double> extractPrices(List<ProductSummary> summaries) {
        return summaries.stream()
                .map(s -> Double.parseDouble(s.price().replace("$", "")))
                .toList();
    }
}
