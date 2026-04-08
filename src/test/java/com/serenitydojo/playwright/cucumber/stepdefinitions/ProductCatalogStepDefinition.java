package com.serenitydojo.playwright.cucumber.stepdefinitions;

import com.microsoft.playwright.Page;
import com.serenitydojo.playwright.cucumber.pages.ProductList;
import com.serenitydojo.playwright.cucumber.pages.ProductSummary;
import com.serenitydojo.playwright.cucumber.pages.Search;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Map;

public class ProductCatalogStepDefinition {

    private Page page;
    private Search search;
    private ProductList productList;

    @Before
    public void setupPageObjects(){

        page = CucumberFixtures.getPage();
        search = new Search(page);
        productList = new ProductList(page);
    }

    @Given("Sally is on the home page")
    public void sally_is_on_the_home_page() {

        page.navigate("https://practicesoftwaretesting.com/");

    }

    @When("she searches for {string}")
    public void she_searches_for(String productName) {

        search.byKeyword(productName);
    }

    @When("filters by the category {string}")
    public void filtering_by_category(String category) {

        search.byCategory(category);
    }

    @When("sorts by {string}")
    public void sorting_by_category(String sort) {

        search.sortBy(sort);
    }

    @Then("no products should be displayed")
    public void no_products_should_be_displayed() {

        List<ProductSummary> actualProductsDisplayed = productList.getDisplayed();
        Assertions.assertThat(actualProductsDisplayed.size()).isEqualTo(0);
    }

    @Then("a message {string} should be displayed")
    public void no_products_should_be_displayed(String expectedMessage) {

        String actualMessage = productList.getMessage();
        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @DataTableType
    public ProductSummary getProductItem(Map<String, String> productData) {

        return new ProductSummary(
                productData.get("Product Name"),
                productData.get("Price")
        );
    }

    @Then("the following products should be displayed:")
    public void the_following_products_displayed(List<ProductSummary> expectedProductsDisplayed) {

        List<ProductSummary> actualProductsDisplayed = productList.getDisplayed();
        Assertions.assertThat(actualProductsDisplayed).containsExactlyInAnyOrderElementsOf(expectedProductsDisplayed);
    }

    @Then("the following products should be displayed in order:")
    public void the_following_products_displayed_in_order(List<ProductSummary> expectedProductsDisplayed) {

        List<ProductSummary> actualProductsDisplayed = productList.getDisplayed();
        Assertions.assertThat(actualProductsDisplayed).containsExactlyElementsOf(expectedProductsDisplayed);
    }
}

